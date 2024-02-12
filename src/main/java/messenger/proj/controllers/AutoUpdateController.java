package messenger.proj.controllers;

import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import messenger.proj.models.ChatRoom;
import messenger.proj.models.message;
import messenger.proj.services.ChatRoomService;
import messenger.proj.services.ConnectionService;
import messenger.proj.services.MessageService;

@Controller
public class AutoUpdateController {

	private final ConnectionService connectionService;
	private final ChatRoomService chatRoomService;
	private final MessageService messageService;

	@Autowired
	public AutoUpdateController(ConnectionService connectionService, MessageService messageService,
			ChatRoomService chatRoomService) {
		this.connectionService = connectionService;
		this.messageService = messageService;
		this.chatRoomService = chatRoomService;
	}

	@GetMapping("/updateUserStatus/{chatId}")
	@ResponseBody
	public String updateUserInfo(@PathVariable("chatId") String userId, Model model) {

		System.out.println("USER ID " + userId);

		String currentUserId = connectionService.getCurrentUserId();
		Optional<ChatRoom> chat = chatRoomService.findById(userId);
		JSONObject jsonObject = new JSONObject();

		if (chat.get().getSenderId().equals(currentUserId)) {
			if (connectionService.getUserConnection(chat.get().getRecipientId()) != null) {
				jsonObject.put("connectionInfo",
						connectionService.getUserConnection(chat.get().getRecipientId()).getOnlineStatus());
			}
		} else if (chat.get().getRecipientId().equals(currentUserId)) {
			if (connectionService.getUserConnection(chat.get().getSenderId()) != null) {
				jsonObject.put("connectionInfo",
						connectionService.getUserConnection(chat.get().getSenderId()).getOnlineStatus());
			}
		}

		System.err.println("TEST " + jsonObject.toString());
		return jsonObject.toString();
	}

	@GetMapping("/updateCassandraMessages/{chatId}")
	@ResponseBody
	public String updateCasssandraMessages(@PathVariable("chatId") String chatId) {
		// Загрузите новые сообщения из вашей службы
		List<message> updatedMessages = messageService.getCassandraMessages(chatId);

		// Возвращаем данные в формате JSON
		return convertMessagesToJson(updatedMessages);
	}
	
	@GetMapping("/updateCachedMessages/{chatId}")
	@ResponseBody
	public List<message> updateCachedMessages(@PathVariable("chatId") String chatId) {
		// Загрузите новые сообщения из вашей службы
		List<message> updatedMessages = messageService.getCaсhedMessages(chatId);
		
		System.out.println("CachedMessages " + updatedMessages);

		// Возвращаем данные в формате JSON
		return updatedMessages;
	}

	public static String convertMessagesToJson(List<message> list) {
		String jsonResult = "";

		// Используйте ObjectMapper для конвертации в JSON
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		try {
			// Преобразование списка объектов в JSON
			jsonResult = objectMapper.writeValueAsString(list);

			// Вывод результата
			System.out.println(jsonResult);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return jsonResult;
	}
}

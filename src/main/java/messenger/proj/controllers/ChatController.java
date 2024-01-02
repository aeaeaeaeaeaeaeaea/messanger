package messenger.proj.controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.multipart.MultipartFile;

import com.datastax.oss.driver.internal.core.util.Strings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import messenger.proj.models.ChatRoom;
import messenger.proj.models.ConnectionInfo;
import messenger.proj.models.FileEntry;
import messenger.proj.models.message;
import messenger.proj.repositories.ElasticSearchQuery;
import messenger.proj.security.PersonDetails;
import messenger.proj.services.ChatRoomService;
import messenger.proj.services.ConnectionService;
import messenger.proj.services.FileService;
import messenger.proj.services.MessageRedisService;
import messenger.proj.services.MessageService;
import messenger.proj.services.UserService;

@Controller
public class ChatController {

	private final MessageService messageServ;
	private final ChatRoomService chatRoomServ;
	private final UserService userServ;
	private final RedisTemplate<String, message> redisTemplate;
	private final ConnectionService connectionServ;
	private final ElasticSearchQuery elasticSearchQuery;
	private final MessageRedisService messageRedisService;
	private final FileService fileService;
	private final String FILE_SAVE_PATH = "C:\\messanger-main\\messanger-main\\src\\main\\resources\\static\\Files\\";

	@Autowired
	public ChatController(ConnectionService connectionServ, RedisTemplate<String, message> redisTemplate,
			MessageRedisService messageRedisService, UserService userServ, ChatRoomService chatRoomServ,
			MessageService messageServ, ElasticSearchQuery elasticSearchQuery, FileService fileService) {

		this.elasticSearchQuery = elasticSearchQuery;
		this.fileService = fileService;
		this.messageRedisService = messageRedisService;
		this.connectionServ = connectionServ;
		this.redisTemplate = redisTemplate;
		this.chatRoomServ = chatRoomServ;
		this.userServ = userServ;
		this.messageServ = messageServ;
	}

	@PostMapping("/chat")
	public ResponseEntity<String> createChat(@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "currentUser", required = false) String currentUser) {

		chatRoomServ.save(currentUser, userId);

		Optional<ChatRoom> chat = chatRoomServ.findBySenderIdAndRecipientId(currentUser, userId);
		Optional<ChatRoom> chat1 = chatRoomServ.findBySenderIdAndRecipientId(userId, currentUser);

		String chatId = chat.isPresent() ? chat.get().getId() : chat1.get().getId();

		return ResponseEntity.ok(new String(chatId));
	}

	// ДЛИНА ПОСЛЕДНЕГО СООБЩЕНИЯ В СПИСКАХ ЧАТОВ (СДЕЛАЮ ПОТОМ)
	@GetMapping("/chat/{userId}")
	public String chat(@PathVariable("userId") String userId, Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

		String curentUserId = personDetails.getUser().getId();
		Optional<ChatRoom> chat = chatRoomServ.findById(userId);
		List<message> list = messageServ.findByChatId(userId);

		chat.get().setUnreadRecipientMessages(0);
		chat.get().setUnreadSenderMessages(0);
		chatRoomServ.chatUnreadMessagesUpdate(chat.get());

		if (!chat.isPresent()) {
			return "redirect:/users";
		}

		if (chat.isPresent() && (!chat.get().getSenderId().equals(curentUserId)
				&& !chat.get().getRecipientId().equals(curentUserId))) {
			return "redirect:/users";

		}

		// Блок кода, который устанавливает recipientId и senderId для сообщений
		if (chat.get().getSenderId().equals(curentUserId)) {
			model.addAttribute("recipientId", chat.get().getRecipientId());
			model.addAttribute("currentUser", chat.get().getSenderId());
		} else if (chat.get().getRecipientId().equals(curentUserId)) {
			model.addAttribute("currentUser", chat.get().getRecipientId());
			model.addAttribute("recipientId", chat.get().getSenderId());
		}
		// Конец блока кода

		// Метод, который делает сообщения прочитанными
		messageServ.readMessages(userId, curentUserId);
		// Метод, который устанавливает статус 'Unread' для сообщений и считает их
		messageServ.setMessageStatus(chat.get(), userId);

		model.addAttribute("unreadSenderMessages", chat.get().getUnreadSenderMessages());
		model.addAttribute("unreadRecipientMessages", chat.get().getUnreadRecipientMessages());
		model.addAttribute("username", userServ.findById(curentUserId).get().getUsername());
		model.addAttribute("f", new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").toFormatter());
		model.addAttribute("todayFormat", new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter());
		model.addAttribute("formatter", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter());
		model.addAttribute("todayDate",
				LocalDateTime.now().format(new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
		model.addAttribute("lastMessages", messageServ.getLastMessage(chatRoomServ.findAll(curentUserId)));
		model.addAttribute("chatList", chatRoomServ.lastMessageOrder(curentUserId));
		model.addAttribute("cachedMessages", messageServ.getCaсhedMessages(userId));
		model.addAttribute("cassandraMessages", list);
		model.addAttribute("id", userId);
        model.addAttribute("files", fileService.getFiles());

		return "chat";
	}

	@PostMapping("/deleteMessage")
	public String deleteMessage(@RequestParam("messageId") String messageId, @RequestParam("chatId") String chatId,
			@RequestParam("sendTime") String sendTime) {

		LocalDateTime ldt = LocalDateTime.parse(sendTime);

		messageServ.deleteById(messageId, ldt, chatId);

		return "redirect:/chat/" + chatId;
	}

	@PostMapping("/editMessage")
	public String editMessage(@RequestParam("messageId") String messageId, @RequestParam("chatId") String chatId,
			@RequestParam("sendTime") String sendTime, @RequestParam("content") String content,
			@RequestParam("senderName") String senderName, @RequestParam("status") String status,
			@RequestParam("senderId") String senderId, @RequestParam("recipientId") String recipientId) {

		LocalDateTime ldt = LocalDateTime.parse(sendTime);

		if (content.trim().isEmpty()) {
			messageServ.deleteById(messageId, ldt, chatId);
			return "redirect:/chat/" + chatId;
		}

		message message = new message();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

		message.setId(messageId);
		message.setContent(content);
		message.setChatId(chatId);
		message.setSendTime(ldt);
		message.setSenderName(senderName);
		message.setSenderId(senderId);
		message.setRecipientId(recipientId);
		message.setStatus(status);

		messageServ.edit(message, messageId);

		return "redirect:/chat/" + chatId;
	}

	@PostMapping("/deleteChat")
	public String deleteChat(@RequestParam(value = "chatId", required = false) String chatId) {

		for (message m : messageServ.getCaсhedMessages(chatId)) {
			messageServ.deleteById(m.getId(), m.getSendTime(), chatId);
		}

		for (message m : messageServ.findByChatId(chatId)) {
			messageServ.deleteById(m.getId(), m.getSendTime(), chatId);
		}

		chatRoomServ.deleteById(chatId);

		return "redirect:/users";
	}

	@PostMapping("/deleteChatMessage")
	public String deleteChatMessages(@RequestParam(value = "chatId", required = false) String chatId) {

		for (message m : messageServ.getCaсhedMessages(chatId)) {
			messageServ.deleteById(m.getId(), m.getSendTime(), chatId);
		}

		for (message m : messageServ.findByChatId(chatId)) {
			messageServ.deleteById(m.getId(), m.getSendTime(), chatId);
		}

		return "redirect:/users";
	}

	@GetMapping("/users")
	public String users(Model model, HttpServletRequest request,
			@RequestParam(value = "userName", required = false) String userName) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
		String curentUserId = personDetails.getUser().getId();

		connectionServ.userConnection(curentUserId, new ConnectionInfo(), request);

		if (userName != null) {
			try {
				model.addAttribute("searchElasticUser", elasticSearchQuery.search(userName));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		model.addAttribute("todayFormat", new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter());
		model.addAttribute("formatter", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter());
		model.addAttribute("todayDate",
				LocalDateTime.now().format(new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));

		model.addAttribute("lastMessages", messageServ.getLastMessage(chatRoomServ.findAll(curentUserId)));

		model.addAttribute("username", userServ.findById(curentUserId).get().getUsername());
		model.addAttribute("currentUser", curentUserId);

		model.addAttribute("chatList", chatRoomServ.lastMessageOrder(curentUserId));

		model.addAttribute("users", userServ.findAll());

		return "message1";
	}

	@PostMapping("/upload-file/{chatId}")
	public String sendMessage(
							  @Payload message message, 
							  @PathVariable("chatId") String chatId,
							  @RequestParam("file") MultipartFile file, 
							  @RequestParam("content") String content,
							  @RequestParam("dataSenderId") String dataSenderId, 
							  @RequestParam("dataRecipId") String dataRecipId)
							  throws JsonMappingException, JsonProcessingException {

		message.setChatId(chatId);
		message.setSenderId(dataSenderId);
		message.setRecipientId(dataRecipId);
		message.setSendTime(LocalDateTime.now());
		message.setStatus("Unread");
		message.setSenderName(userServ.findById(dataSenderId).get().getUsername());

		messageServ.save(chatId, message);

		if (file != null && !file.isEmpty()) {
			FileEntry fileEntry = new FileEntry();
			fileEntry.setId(UUID.randomUUID().toString());
			fileEntry.setMessageId(message.getId());
			fileEntry.setPath(FILE_SAVE_PATH + file.getOriginalFilename());
			fileEntry.setFileName(file.getOriginalFilename());
			
			fileService.save(fileEntry);
			
			try {
				fileService.saveFileToServer(file, FILE_SAVE_PATH);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// Дополнительная логика сохранения сообщения и отправки в WebSocket

		return "redirect:/chat/" + chatId;
	}
}

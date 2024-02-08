package messenger.proj.controllers;

import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import messenger.proj.models.ChatRoom;
import messenger.proj.services.ChatRoomService;
import messenger.proj.services.ConnectionService;

@Controller
public class AutoUpdateController {
	
	private final ConnectionService connectionService;
	private final ChatRoomService chatRoomService;
	
	@Autowired
	public AutoUpdateController(ConnectionService connectionService, ChatRoomService chatRoomService) {
		this.connectionService = connectionService;
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

}

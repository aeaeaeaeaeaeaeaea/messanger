package messenger.proj.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import messenger.proj.DTO.MessageDTO;
import messenger.proj.models.Message;
import messenger.proj.services.ChatRoomService;
import messenger.proj.services.ConnectionService;
import messenger.proj.services.MessageService;
import messenger.proj.services.UserService;

@RestController
@RequestMapping("/message")
public class MessageController {


	private MessageService messageService;
	private ChatRoomService chatRoomService;
	private UserService userService;
	private ConnectionService connectionService;

	@Autowired
	public MessageController(UserService userService, ConnectionService connectionService, 
			MessageService messageService,
			ChatRoomService chatRoomService) {
		this.userService = userService;
		this.connectionService = connectionService;
		
		this.messageService = messageService;
		this.chatRoomService = chatRoomService;
	}

	@PostMapping("/chat/{chatId}/sendMessage")
	public ResponseEntity<String> processChatMessage(@RequestBody MessageDTO messageDTO, @PathVariable("chatId") String chatId) {
		
		String currentUserId = connectionService.getCurrentUserId();
		
		
		if (!messageDTO.getContent().equals("")) {
			
			Message message = messageService.save(messageDTO, chatId, currentUserId);
			
			connectionService.userConnection(message.getSenderId(),
					connectionService.getUserConnection(message.getSenderId()), message.getRecipientId());
		}
		
		return ResponseEntity.ok("Message has been sent");

	}

}

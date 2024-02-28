package messenger.proj.controllers;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import messenger.proj.models.ChatRoom;
import messenger.proj.models.ConnectionInfo;
import messenger.proj.models.FileEntry;
import messenger.proj.models.User;
import messenger.proj.models.message;
import messenger.proj.security.PersonDetails;
import messenger.proj.services.ChatRoomService;
import messenger.proj.services.ConnectionService;
import messenger.proj.services.MessageService;
import messenger.proj.services.UserService;

@RestController
@RequestMapping("/message")
public class MessageController {


	private MessageService messageServ;
	private ChatRoomService chatRoomServ;
	private UserService userServ;
	private ConnectionService connectionService;

	@Autowired
	public MessageController(UserService userServ, ConnectionService connectionService, MessageService messageServ,
			ChatRoomService chatRoomServ) {
		this.userServ = userServ;
		this.connectionService = connectionService;
		
		this.messageServ = messageServ;
		this.chatRoomServ = chatRoomServ;
	}

	@PostMapping("/chat/{chatId}/sendMessage")
	public ResponseEntity<String> processChatMessage(@RequestBody message message, @PathVariable("chatId") String chatId) {
		
		String currentUserId = connectionService.getCurrentUserId();
		
		
		if (!message.getContent().equals("")) {
			
			messageServ.save(message, chatId, currentUserId);
			
			//connectionService.userConnection(senderId, connectionService.getUserConnection(senderId), recipId);
		}
		
		return ResponseEntity.ok("Message has been sent");

	}

}

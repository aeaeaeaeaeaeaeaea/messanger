package messenger.proj.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import messenger.proj.models.User;
import messenger.proj.models.message;
import messenger.proj.services.ChatRoomService;
import messenger.proj.services.MessageService;
import messenger.proj.services.UserService;


@Controller
public class MessageController {

	private SimpMessagingTemplate messagingTemplate;
	private MessageService messageServ;
	private ChatRoomService chatRoomServ;
	private UserService userServ;
	
	@Autowired
	public MessageController(UserService userServ, SimpMessagingTemplate messagingTemplate, MessageService messageServ,
			ChatRoomService chatRoomServ) {
		this.userServ = userServ;
		this.messagingTemplate = messagingTemplate;
		this.messageServ = messageServ;
		this.chatRoomServ = chatRoomServ;
	}
	
	@MessageMapping("/chat/{chatId}/sendMessage")
	@SendTo("/topic/{chatId}")
	public void processChatMessage(@Payload message message, @PathVariable("chatId") String chatId) throws JsonMappingException, JsonProcessingException {
		
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode jsonNode = objectMapper.readTree(chatId);

	        String extractedChatId = jsonNode.get("chatId").asText();
	        System.out.println("Extracted Chat id: " + extractedChatId);

	        message.setChatId(extractedChatId);
	        messageServ.save(message);

	        messagingTemplate.convertAndSend("/topic/" + extractedChatId, message);
	    
	}

	
	@GetMapping("/users")
	public String users(Model model) {
		
		model.addAttribute("users", userServ.findAll());
		
		return "message1";
	}
	
	@GetMapping("/chat/{userId}")
	public String chat(@PathVariable("userId") String userId, Model model) {
		
		model.addAttribute("messages", messageServ.findByChatId(userId));
		
		model.addAttribute("id", userId);
			
		return "chat";
	}
}

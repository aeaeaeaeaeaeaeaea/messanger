package messenger.proj.controllers;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import messenger.proj.models.ChatRoom;
import messenger.proj.models.FileEntry;
import messenger.proj.models.User;
import messenger.proj.models.message;
import messenger.proj.security.PersonDetails;
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
	public void processChatMessage(@Payload message message, 
								   @PathVariable("chatId") String chatId) 
								   throws JsonMappingException, JsonProcessingException, IOException {
		
		if (!message.getContent().equals("")) {
			
			System.out.println("MESSAGE CHAT ID  " + chatId);

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());

			JsonNode jsonNode = objectMapper.readTree(chatId);
			String extractedChatId = jsonNode.get("chatId").asText();
			String senderId = jsonNode.get("dataSenderId").asText();
			String recipId = jsonNode.get("dataRecipId").asText();

			message.setChatId(extractedChatId);
			message.setSenderId(senderId);
			message.setRecipientId(recipId);
			message.setSendTime(LocalDateTime.now());
			message.setStatus("Unread");
			message.setSenderName(userServ.findById(senderId).get().getUsername());

			messageServ.save(extractedChatId, message);

			messagingTemplate.convertAndSend("/topic/" + extractedChatId, message);

		}

	}

}

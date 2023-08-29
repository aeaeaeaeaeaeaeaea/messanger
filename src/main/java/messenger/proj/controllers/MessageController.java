package messenger.proj.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public void processChatMessage(@Payload message message, @PathVariable("chatId") String chatId)
			throws JsonMappingException, JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(chatId);

		String extractedChatId = jsonNode.get("chatId").asText();
		String senderId = jsonNode.get("dataSenderId").asText();

		message.setChatId(extractedChatId);
		message.setSenderId(senderId);
		messageServ.save(message);

		messagingTemplate.convertAndSend("/topic/" + extractedChatId, message);

	}

	@GetMapping("/users")
	public String users(Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

		model.addAttribute("currentUser", personDetails.getUser().getId());
		model.addAttribute("users", userServ.findAll());

		return "message1";
	}

	@GetMapping("/chat/{userId}")
	public String chat(@PathVariable("userId") String userId, Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

		model.addAttribute("messages", messageServ.findByChatId(userId));
		model.addAttribute("currentUser", personDetails.getUser().getId());
		model.addAttribute("id", userId);

		return "chat";
	}

	@PostMapping("/deleteMessage")
	public String deleteMessage(@RequestParam("messageId") String messageId, @RequestParam("chatId") String chatId) {

		messageServ.deleteById(messageId);

		return "redirect:/chat/" + chatId;
	}

	@PostMapping("/editMessage")
	public String editMessage(@ModelAttribute("message") message message, @RequestParam("chatId") String chatId,
			@RequestParam("messageId") String messageId, @RequestParam("editedContent") String editedContent) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

		message.setId(messageId);
		message.setContent(editedContent);
		message.setChatId(chatId);
		message.setSenderId(personDetails.getUser().getId());
		
		messageServ.edit(message, messageId);

		return "redirect:/chat/" + chatId;
	}

}

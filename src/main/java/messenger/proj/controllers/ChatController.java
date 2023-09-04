package messenger.proj.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import messenger.proj.models.ChatRoom;
import messenger.proj.models.message;
import messenger.proj.security.PersonDetails;
import messenger.proj.services.ChatRoomService;
import messenger.proj.services.MessageService;
import messenger.proj.services.UserService;

@Controller
public class ChatController {
		
	private SimpMessagingTemplate messagingTemplate;
	private MessageService messageServ;
	private ChatRoomService chatRoomServ;
	private UserService userServ;
	
	@Autowired
	public ChatController(ChatRoomService chatRoomServ, UserService userServ, SimpMessagingTemplate messagingTemplate, MessageService messageServ) {
		super();
		this.chatRoomServ = chatRoomServ;
		this.userServ = userServ;
		this.messagingTemplate = messagingTemplate;
		this.messageServ = messageServ;
	}
	
	@PostMapping("/chat")
	public String createChat(@RequestParam("userId") String userId, @RequestParam("currentUser") String currentUser) {
		
		System.out.println("TEST: " + currentUser);
		
		chatRoomServ.save(userId, currentUser);
		
		Optional<ChatRoom> chat = chatRoomServ.findBySenderIdAndRecipientId(currentUser, userId);
		
		return "redirect:/chat/" + chat.get().getId();
	}
	
	@GetMapping("/chat/{userId}")
	public String chat(@PathVariable("userId") String userId, Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
		
		String curentUserId = personDetails.getUser().getId();

	
		model.addAttribute("messages", messageServ.findByChatId(userId));
		
		model.addAttribute("currentUser", curentUserId);
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

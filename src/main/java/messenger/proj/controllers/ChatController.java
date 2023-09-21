package messenger.proj.controllers;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
import messenger.proj.services.ConnectionService;
import messenger.proj.services.MessageService;
import messenger.proj.services.UserService;

@Controller
public class ChatController {

	private final MessageService messageServ;
	private final ChatRoomService chatRoomServ;
	private UserService userServ;
	private final RedisTemplate<String, message> redisTemplate;
	private final ConnectionService connectionServ;

	@Autowired
	public ChatController(ConnectionService connectionServ, RedisTemplate<String, message> redisTemplate, UserService userServ,
			ChatRoomService chatRoomServ, MessageService messageServ) {
		super();
		this.connectionServ = connectionServ;
		this.redisTemplate = redisTemplate;
		this.chatRoomServ = chatRoomServ;
		this.userServ = userServ;
		this.messageServ = messageServ;
	}

	@PostMapping("/chat")
	public String createChat(@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "currentUser", required = false) String currentUser) {

		chatRoomServ.save(userId, currentUser);

		Optional<ChatRoom> chat = chatRoomServ.findBySenderIdAndRecipientId(currentUser, userId);
		Optional<ChatRoom> chat1 = chatRoomServ.findBySenderIdAndRecipientId(userId, currentUser);

		if (chat.isPresent()) {
			return "redirect:/chat/" + chat.get().getId();
		}

		return "redirect:/chat/" + chat1.get().getId();
	}

	@GetMapping("/chat/{userId}")
	public String chat(@PathVariable("userId") String userId, Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

		String curentUserId = personDetails.getUser().getId();

		Optional<ChatRoom> chat = chatRoomServ.findById(userId);

		if (!chat.isPresent()) {
			return "redirect:/users";
		}

		if (chat.isPresent() && (!chat.get().getSenderId().equals(curentUserId)
				&& !chat.get().getRecipientId().equals(curentUserId))) {
			return "redirect:/users";

		}

		model.addAttribute("cachedMessages", messageServ.getCaсhedMessages(userId));
		model.addAttribute("cassandraMessages", messageServ.findByChatId(userId));

		model.addAttribute("currentUser", curentUserId);
		model.addAttribute("id", userId);

		return "chat";
	}

	@PostMapping("/deleteMessage")
	public String deleteMessage(@RequestParam("messageId") String messageId, @RequestParam("chatId") String chatId) {

		messageServ.deleteById(messageId, chatId);
		System.out.println("DELETE MESSAGE: " + messageId);

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

	@PostMapping("/deleteChat")
	public String deleteChat(@RequestParam(value = "chatId", required = false) String chatId) {

		for (message m : messageServ.getCaсhedMessages(chatId)) {
			messageServ.deleteById(m.getId(), chatId);
		}

		for (message m : messageServ.findByChatId(chatId)) {
			messageServ.deleteById(m.getId(), chatId);
		}

		chatRoomServ.deleteById(chatId);

		return "redirect:/users";
	}

	@PostMapping("/deleteChatMessage")
	public String deleteChatMessages(@RequestParam(value = "chatId", required = false) String chatId) {

		for (message m : messageServ.getCaсhedMessages(chatId)) {
			messageServ.deleteById(m.getId(), chatId);
		}

		for (message m : messageServ.findByChatId(chatId)) {
			messageServ.deleteById(m.getId(), chatId);
		}

		return "redirect:/users";
	}

	@GetMapping("/users")
	public String users(Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
		String id = personDetails.getUser().getId();
		
		connectionServ.userConnection(id, userServ.findById(id).get());

		model.addAttribute("currentUser", id);
		model.addAttribute("chatList", chatRoomServ.findAll(id));
		model.addAttribute("users", userServ.findAll());

		return "message1";
	}
}

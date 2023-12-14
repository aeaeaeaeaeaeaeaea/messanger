package messenger.proj.controllers;

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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
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

import jnr.ffi.Struct.int16_t;
import messenger.proj.models.ChatRoom;
import messenger.proj.models.ConnectionInfo;
import messenger.proj.models.message;
import messenger.proj.repositories.ElasticSearchQuery;
import messenger.proj.security.PersonDetails;
import messenger.proj.services.ChatRoomService;
import messenger.proj.services.ConnectionService;
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
	private int countUnreadMessages = 0;

	@Autowired
	public ChatController(ConnectionService connectionServ, RedisTemplate<String, message> redisTemplate,
			MessageRedisService messageRedisService, UserService userServ, ChatRoomService chatRoomServ,
			MessageService messageServ, ElasticSearchQuery elasticSearchQuery) {

		this.elasticSearchQuery = elasticSearchQuery;
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

		
		
		
		
		if (chat.get().getSenderId().equals(curentUserId)) {
			model.addAttribute("recipientId", chat.get().getRecipientId());
			model.addAttribute("currentUser", chat.get().getSenderId());
		} else if (chat.get().getRecipientId().equals(curentUserId)) {			
			model.addAttribute("currentUser", chat.get().getRecipientId());
			model.addAttribute("recipientId", chat.get().getSenderId());
		}
		
	

		model.addAttribute("unreadMessages", countUnreadMessages);

		List<message> list = messageServ.findByChatId(userId);

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
			@RequestParam("senderName") String senderName) {

		/*
		 * System.out.println("CHAT ID " + chatId); System.out.println("MESSAGE ID " +
		 * messageId); System.out.println("SEND TIME " + sendTime);
		 */
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
		message.setSenderId(personDetails.getUser().getId());

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

		

		

		model.addAttribute("unreadMessages", countUnreadMessages);

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
}

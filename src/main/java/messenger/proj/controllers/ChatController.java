package messenger.proj.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
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
import messenger.proj.models.User;
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
	private final ConnectionService connectionService;
	private final ElasticSearchQuery elasticSearchQuery;
	private final MessageRedisService messageRedisService;
	private final FileService fileService;
	private final String FILE_SAVE_PATH = "C:\\messanger-main\\messanger-main\\src\\main\\resources\\static\\Files\\";

	@Autowired
	public ChatController(ConnectionService connectionService, RedisTemplate<String, message> redisTemplate,
			MessageRedisService messageRedisService, UserService userServ, ChatRoomService chatRoomServ,
			MessageService messageServ, ElasticSearchQuery elasticSearchQuery, FileService fileService) {

		this.elasticSearchQuery = elasticSearchQuery;
		this.fileService = fileService;
		this.messageRedisService = messageRedisService;
		this.connectionService = connectionService;
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

	// Страница с чатом между 2-мя пользователями
	// ДЛИНА ПОСЛЕДНЕГО СООБЩЕНИЯ В СПИСКАХ ЧАТОВ (СДЕЛАЮ ПОТОМ)
	@GetMapping("/chat/{userId}")
	public String chat(@PathVariable("userId") String userId, Model model, HttpServletRequest request) {

		model.addAttribute("chatId", userId);
		String currentUserId = connectionService.getCurrentUserId();

		connectionService.setCurrentPageForUserConnection(currentUserId,
				connectionService.getUserConnection(currentUserId),
				(String) request.getSession().getAttribute("currentMapping"));

		// Получаем чат по его Id
		Optional<ChatRoom> chat = chatRoomServ.findById(userId);

		// Редирект на главную страницу, если чат не существует
		chatRoomServ.reidrectIfChatRoomDontExist(chat, currentUserId);

		// Читаем сообщения со статусом Unread и устанавливаем для них статус Read
		messageServ.readMessages(userId, currentUserId, chat.get(), request);

		// Устанавливаем статус 'Unread' для сообщений и считаем их
		messageServ.setMessageStatus(chat.get(), userId);

		// Устанавливаем recipientId и senderId для сообщений
		if (chat.get().getSenderId().equals(currentUserId)) {

			if (connectionService.getUserConnection(chat.get().getRecipientId()) != null) {
				model.addAttribute("connectionInfo",
						connectionService.getUserConnection(chat.get().getRecipientId()).getOnlineStatus());
			}

			User currentUser = userServ.findById(chat.get().getSenderId()).get();
			User recipientUser = userServ.findById(chat.get().getRecipientId()).get();

			model.addAttribute("recipientId", chat.get().getRecipientId());
			model.addAttribute("currentUser", chat.get().getSenderId());
			model.addAttribute("recipientUserName", userServ.findById(chat.get().getRecipientId()).get().getUsername());
			model.addAttribute("recipientUserPhoneNumber", recipientUser.getPhoneNumber());
			model.addAttribute("currentUserPhoneNumber", currentUser.getPhoneNumber());
			model.addAttribute("currentUserAvatar", userServ.getCurrentUserAvatar(currentUser));
			model.addAttribute("recipientUserAvatar", userServ.getRecipientUserAvatar(recipientUser));

		} else if (chat.get().getRecipientId().equals(currentUserId)) {

			if (connectionService.getUserConnection(chat.get().getSenderId()) != null) {
				model.addAttribute("connectionInfo",
						connectionService.getUserConnection(chat.get().getSenderId()).getOnlineStatus());
			}

			User currentUser = userServ.findById(chat.get().getRecipientId()).get();
			User recipientUser = userServ.findById(chat.get().getSenderId()).get();

			model.addAttribute("recipientUserPhoneNumber", recipientUser.getPhoneNumber());
			model.addAttribute("currentUserPhoneNumber", currentUser.getPhoneNumber());
			model.addAttribute("currentUserAvatar", userServ.getCurrentUserAvatar(currentUser));
			model.addAttribute("recipientUserAvatar", userServ.getRecipientUserAvatar(recipientUser));
			model.addAttribute("currentUser", chat.get().getRecipientId());
			model.addAttribute("recipientId", chat.get().getSenderId());
			model.addAttribute("recipientUserName", userServ.findById(chat.get().getSenderId()).get().getUsername());

		}

		model.addAttribute("unreadSenderMessages", chat.get().getUnreadSenderMessages());
		model.addAttribute("unreadRecipientMessages", chat.get().getUnreadRecipientMessages());
		model.addAttribute("username", userServ.findById(currentUserId).get().getUsername());

		model.addAttribute("f", new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").toFormatter());
		model.addAttribute("todayFormat", new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter());
		model.addAttribute("formatter", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter());
		model.addAttribute("todayDate",
				LocalDateTime.now().format(new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));

		model.addAttribute("lastMessages", messageServ.getLastMessage(chatRoomServ.findAll(currentUserId)));
		model.addAttribute("chatList", chatRoomServ.lastMessageOrder(currentUserId));
		model.addAttribute("cachedMessages", messageServ.getCaсhedMessages(userId));
		model.addAttribute("cassandraMessages", messageServ.findByChatId(userId));
		model.addAttribute("id", userId);
		model.addAttribute("files", fileService.getFiles());

		return "chat";
	}

	// Удаления сообщений
	@PostMapping("/deleteMessage")
	public String deleteMessage(@RequestParam("messageId") String messageId, @RequestParam("chatId") String chatId,
			@RequestParam("sendTime") String sendTime) {

		// LocalDateTime нужен потому что, время сообщения входят в составной primary
		// key (без этого сообщения не сортируются) и без него мы не
		// сможем удалить сообщение
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime ldt = LocalDateTime.parse(sendTime, formatter);

		// Удаляем сообщения по ID
		messageServ.deleteById(messageId, ldt, chatId);

		return "redirect:/chat/" + chatId;
	}

	// Редактируем сообещения
	@PostMapping("/editMessage")
	public String editMessage(@RequestParam("messageId") String messageId, @RequestParam("chatId") String chatId,
			@RequestParam("sendTime") String sendTime, @RequestParam("content") String content,
			@RequestParam("senderName") String senderName, @RequestParam("status") String status,
			@RequestParam("senderId") String senderId, @RequestParam("recipientId") String recipientId) {

		// LocalDateTime нужен потому что, время сообщения входят в составной primary
		// key (без этого сообщения не сортируются) и без него мы не
		// сможем удалить сообщение
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime ldt = LocalDateTime.parse(sendTime, formatter);

		// Если сообщения состоит только из пробелов и у него нет файла, то оно
		// удаляется при редактировании
		if (content.trim().isEmpty() && !fileService.getFiles().containsKey(messageId)) {
			// Удаляем сообщение по ID
			messageServ.deleteById(messageId, ldt, chatId);
			return "redirect:/chat/" + chatId;
		}

		// Редактируем сообщение
		messageServ.edit(messageId, content, chatId, ldt, senderName, senderId, recipientId, status);

		return "redirect:/chat/" + chatId;
	}

	// Удаляем чат
	@PostMapping("/deleteChat")
	public String deleteChat(@RequestParam(value = "chatId", required = false) String chatId) {

		// Удаляем все сообщения из чата
		messageServ.deleteAllMessagesFromChat(chatId);

		// Удаляем чат
		chatRoomServ.deleteById(chatId);

		return "redirect:/users";
	}

	// Очищаем все сообщения из чата
	@PostMapping("/deleteChatMessage")
	public String deleteChatMessages(@RequestParam(value = "chatId", required = false) String chatId) {

		// Удаляем все сообщения из чата
		messageServ.deleteAllMessagesFromChat(chatId);

		return "redirect:/users";
	}

	// Страница со всеми чатами
	@GetMapping("/users")
	public String users(Model model, @RequestParam(value = "userName", required = false) String userName,
			HttpServletRequest request) {

		String currentUserId = connectionService.getCurrentUserId();

		connectionService.setCurrentPageForUserConnection(currentUserId,
				connectionService.getUserConnection(currentUserId),
				(String) request.getSession().getAttribute("currentMapping"));

		if (userName != null) {
			try {
				model.addAttribute("searchElasticUser", elasticSearchQuery.search(userName));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// Кринж?
		// connectionService.setUserOffline(currentUserId);

		model.addAttribute("todayFormat", new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter());
		model.addAttribute("formatter", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter());
		model.addAttribute("todayDate",
				LocalDateTime.now().format(new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));

		model.addAttribute("lastMessages", messageServ.getLastMessage(chatRoomServ.findAll(currentUserId)));
		model.addAttribute("username", userServ.findById(currentUserId).get().getUsername());
		model.addAttribute("currentUser", currentUserId);
		model.addAttribute("chatList", chatRoomServ.lastMessageOrder(currentUserId));
		model.addAttribute("files", fileService.getFiles());
		model.addAttribute("users", userServ.findAll());

		return "message1";
	}

	// Загружаем файл
	@PostMapping("/upload-file/{chatId}")
	public String sendMessage(@Payload message message, @PathVariable("chatId") String chatId,
			@RequestParam("file") MultipartFile file, @RequestParam("content") String content,
			@RequestParam("dataSenderId") String dataSenderId, @RequestParam("dataRecipId") String dataRecipId)
			throws JsonMappingException, JsonProcessingException {

		System.err.println("FILE TEST ");

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

		return "redirect:/chat/" + chatId;
	}

	@PostMapping("/avatar")
	public String avatar(@RequestPart("file") MultipartFile file) {

		String currentUserId = connectionService.getCurrentUserId();

		User user = userServ.findById(currentUserId).get();

		try {
			if (!file.getOriginalFilename().split("\\.")[1].toLowerCase().equals("mp4")
					&& !file.getOriginalFilename().split("\\.")[1].toLowerCase().equals("mov")
					&& !file.getOriginalFilename().split("\\.")[1].toLowerCase().equals("mob")
					&& !file.getOriginalFilename().split("\\.")[1].toLowerCase().equals("mkv")
					&& !file.getOriginalFilename().split("\\.")[1].toLowerCase().equals("m4b")
					&& !file.getOriginalFilename().split("\\.")[1].toLowerCase().equals("avi")) {
				user.setAvatar(ByteBuffer.wrap(file.getBytes()));
				userServ.editUser(user);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/users";
	}

	@PostMapping("/cachedTest")
	public String teString(Model model) {
		String userId = "0cbbb13e-80d2-4993-a371-fde9a3aaab3c";

		model.addAttribute("todayFormat", new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter());
		model.addAttribute("formatter", new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter());
		model.addAttribute("todayDate",
				LocalDateTime.now().format(new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy").toFormatter()));
		model.addAttribute("cachedMessages", messageServ.getCaсhedMessages(userId));
		model.addAttribute("cassandraMessages", messageServ.findByChatId(userId));

		model.addAttribute("files", fileService.getFiles());
		return "NewFile :: test_frag";
	}

}

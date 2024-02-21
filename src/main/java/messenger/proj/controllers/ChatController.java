package messenger.proj.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import messenger.proj.models.ChatRoom;

import messenger.proj.models.message;
import messenger.proj.repositories.ElasticSearchQuery;

import messenger.proj.services.ChatRoomService;
import messenger.proj.services.ConnectionService;
import messenger.proj.services.FileService;
import messenger.proj.services.MessageRedisService;
import messenger.proj.services.MessageService;
import messenger.proj.services.UserService;

@RestController
@RequestMapping("/k")
public class ChatController {

	private final MessageService messageServ;
	private final ChatRoomService chatRoomServ;
	private final UserService userServ;
	private final RedisTemplate<String, message> redisTemplate;
	private final ConnectionService connectionService;
	private final ElasticSearchQuery elasticSearchQuery;
	private final MessageRedisService messageRedisService;
	private final FileService fileService;

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

	// Страница со всеми чатами
	@GetMapping("/chats")
	public List<ChatRoom> users(HttpServletRequest request) {

		String currentUserId = connectionService.getCurrentUserId();
		connectionService.setCurrentPage(currentUserId, connectionService.getUserConnection(currentUserId),
				(String) request.getSession().getAttribute("currentMapping"));

		return chatRoomServ.findUsersChats(currentUserId);
	}

	// Создание нового чата
	@PostMapping("/createChat")
	public ResponseEntity<String> createChat(@RequestBody ChatRoom chatRoom) {
		chatRoomServ.save(chatRoom);
		return ResponseEntity.ok("Chat was created!");
	}

	// Удаляем чат
	@DeleteMapping("/deleteChat/{chatId}")
	public ResponseEntity<String> deleteChat(@PathVariable("chatId") String chatId) {
		// Удаляем все сообщения из чата
		messageServ.deleteAllMessagesFromChat(chatId);
		// Удаляем чат
		chatRoomServ.deleteById(chatId);
		return ResponseEntity.ok("Chat was deleted!");
	}

	// Очищаем все сообщения из чата
	@DeleteMapping("/deleteChatMessage/{chatId}")
	public ResponseEntity<String> deleteChatMessages(@PathVariable("chatId") String chatId) {
		// Удаляем все сообщения из чата
		messageServ.deleteAllMessagesFromChat(chatId);
		return ResponseEntity.ok("Chat messages were deleted!");
	}

	// Страница с чатом между 2-мя пользователями
	@GetMapping("/chat/{chatId}")
	public List<message> chat(@PathVariable("chatId") String chatId, HttpServletRequest request) {

		String currentUserId = connectionService.getCurrentUserId();

		connectionService.setCurrentPage(currentUserId, connectionService.getUserConnection(currentUserId),
				(String) request.getSession().getAttribute("currentMapping"));
		/*
		 * // Получаем чат по его Id Optional<ChatRoom> chat =
		 * chatRoomServ.findById(chatId);
		 * 
		 * 
		 * 
		 * // Читаем сообщения со статусом Unread и устанавливаем для них статус Read
		 * messageServ.readMessages(chatId, currentUserId, chat.get(), request);
		 * 
		 * // Устанавливаем статус 'Unread' для сообщений и считаем их
		 * messageServ.setMessageStatus(chat.get(), chatId);
		 */

		return messageServ.findByChatId(chatId);
	}

	// Удаления сообщений
	@DeleteMapping("/deleteMessage")
	public ResponseEntity<String> deleteMessage(@RequestBody message message) {
		
		System.err.println("TEST");

		// Удаляем сообщения по ID
		messageServ.deleteById(message);

		return ResponseEntity.ok("Message was deleted");
	}

	// Редактируем сообещения
	@DeleteMapping("/editMessage")
	public String editMessage(@RequestParam("messageId") String messageId, @RequestParam("chatId") String chatId,
			@RequestParam("sendTime") String sendTime, @RequestParam("content") String content,
			@RequestParam("senderName") String senderName, @RequestParam("status") String status,
			@RequestParam("senderId") String senderId, @RequestParam("recipientId") String recipientId) {

		/*
		 * // LocalDateTime нужен потому что, время сообщения входят в составной primary
		 * // key (без этого сообщения не сортируются) и без него мы не // сможем
		 * удалить сообщение DateTimeFormatter formatter =
		 * DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); LocalDateTime ldt =
		 * LocalDateTime.parse(sendTime, formatter);
		 * 
		 * // Если сообщения состоит только из пробелов и у него нет файла, то оно //
		 * удаляется при редактировании if (content.trim().isEmpty() &&
		 * !fileService.getFiles().containsKey(messageId)) { // Удаляем сообщение по ID
		 * messageServ.deleteById(messageId, ldt, chatId);
		 * 
		 * }
		 * 
		 * // Редактируем сообщение messageServ.edit(messageId, content, chatId, ldt,
		 * senderName, senderId, recipientId, status);
		 */

		return "";
	}

	/*
	 * // Загружаем файл
	 * 
	 * @PostMapping("/upload-file/{chatId}") public String sendMessage(@Payload
	 * message message, @PathVariable("chatId") String chatId,
	 * 
	 * @RequestParam("file") MultipartFile file, @RequestParam("content") String
	 * content,
	 * 
	 * @RequestParam("dataSenderId") String
	 * dataSenderId, @RequestParam("dataRecipId") String dataRecipId) throws
	 * JsonMappingException, JsonProcessingException {
	 * 
	 * message.setChatId(chatId); message.setSenderId(dataSenderId);
	 * message.setRecipientId(dataRecipId);
	 * message.setSendTime(LocalDateTime.now()); message.setStatus("Unread");
	 * message.setSenderName(userServ.findById(dataSenderId).get().getUsername());
	 * 
	 * messageServ.save(chatId, message);
	 * 
	 * if (file != null && !file.isEmpty()) { FileEntry fileEntry = new FileEntry();
	 * fileEntry.setId(UUID.randomUUID().toString());
	 * fileEntry.setMessageId(message.getId()); fileEntry.setPath(FILE_SAVE_PATH +
	 * file.getOriginalFilename());
	 * fileEntry.setFileName(file.getOriginalFilename());
	 * 
	 * fileService.save(fileEntry);
	 * 
	 * try { fileService.saveFileToServer(file, FILE_SAVE_PATH); } catch
	 * (IOException e) { e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * return "redirect:/chat/" + chatId; }
	 */

}

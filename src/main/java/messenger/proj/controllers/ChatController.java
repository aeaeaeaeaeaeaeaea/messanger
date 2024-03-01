package messenger.proj.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import messenger.proj.DTO.MessageDTO;
import messenger.proj.models.ChatRoom;
import messenger.proj.models.Message;
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

	private final MessageService messageService;
	private final ChatRoomService chatRoomService;
	private final UserService userService;
	private final RedisTemplate<String, Message> redisTemplate;
	private final ConnectionService connectionService;
	private final ElasticSearchQuery elasticSearchQuery;
	private final MessageRedisService messageRedisService;
	private final FileService fileService;

	@Autowired
	public ChatController(ConnectionService connectionService, RedisTemplate<String, Message> redisTemplate,
			MessageRedisService messageRedisService, UserService userService, ChatRoomService chatRoomService,
			MessageService messageService, ElasticSearchQuery elasticSearchQuery, FileService fileService) {

		this.elasticSearchQuery = elasticSearchQuery;
		this.fileService = fileService;
		this.messageRedisService = messageRedisService;
		this.connectionService = connectionService;
		this.redisTemplate = redisTemplate;
		this.chatRoomService = chatRoomService;
		this.userService = userService;
		this.messageService = messageService;
	}

	// Страница со всеми чатами
	@GetMapping("/chats")
	public List<ChatRoom> users(HttpServletRequest request) {

		String currentUserId = connectionService.getCurrentUserId();
		connectionService.setCurrentPage(currentUserId, connectionService.getUserConnection(currentUserId),
				(String) request.getSession().getAttribute("currentMapping"));

		return chatRoomService.findUsersChats(currentUserId);
	}

	// Создание нового чата
	@PostMapping("/createChat")
	public ResponseEntity<String> createChat(@RequestBody ChatRoom chatRoom) {
		chatRoomService.save(chatRoom);
		return ResponseEntity.ok("Chat was created!");
	}

	// Удаляем чат
	@DeleteMapping("/deleteChat/{chatId}")
	public ResponseEntity<String> deleteChat(@PathVariable("chatId") String chatId) {
		// Удаляем все сообщения из чата
		messageService.deleteAllMessagesFromChat(chatId);
		// Удаляем чат
		chatRoomService.deleteById(chatId);
		return ResponseEntity.ok("Chat was deleted!");
	}

	// Очищаем все сообщения из чата
	@DeleteMapping("/deleteChatMessage/{chatId}")
	public ResponseEntity<String> deleteChatMessages(@PathVariable("chatId") String chatId) {
		// Удаляем все сообщения из чата
		messageService.deleteAllMessagesFromChat(chatId);
		return ResponseEntity.ok("Chat messages were deleted!");
	}

	// Страница с чатом между 2-мя пользователями
	@GetMapping("/chat/{chatId}")
	public List<MessageDTO> chat(@PathVariable("chatId") String chatId, HttpServletRequest request) {

		String currentUserId = connectionService.getCurrentUserId();

		connectionService.setCurrentPage(currentUserId, connectionService.getUserConnection(currentUserId),
				(String) request.getSession().getAttribute("currentMapping"));

		// Получаем чат по его Id
		Optional<ChatRoom> chat = chatRoomService.findById(chatId);
		// Читаем сообщения со статусом Unread и устанавливаем для них статус Read
		messageService.readMessages(currentUserId, chat.get(), request);

		return messageService.findByChatIdDTO(chatId);
	}

	// Удаления сообщений
	@DeleteMapping("/deleteMessage")
	public ResponseEntity<String> deleteMessage(@RequestBody MessageDTO message) {
		// Удаляем сообщения по ID
		messageService.deleteById(message);
		return ResponseEntity.ok("The message has been deleted");
	}

	// Редактируем сообщение
	@PatchMapping("/editMessage")
	public ResponseEntity<String> editMessage(@RequestBody MessageDTO message) {

		/*
		 * // Если сообщения состоит только из пробелов и у него нет файла, то оно удаляется при редактировании 
		 * if (content.trim().isEmpty() && !fileService.getFiles().containsKey(messageId)) { 
		 * 		// Удаляем сообщение по ID
		 * 		messageServ.deleteById(messageId, ldt, chatId);
		 * }
		 */

		// Если сообщения состоит только из пробелов, то оно удаляется при редактировании
		if (message.getContent().trim().isEmpty()) {
			messageService.deleteById(message);
			return ResponseEntity.ok("The message has been deleted because it was empty");
		}

		messageService.edit(message);
		return ResponseEntity.ok("The message has been edited!");
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

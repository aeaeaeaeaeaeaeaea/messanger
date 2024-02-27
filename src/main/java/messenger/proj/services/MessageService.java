package messenger.proj.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.elasticsearch.cluster.coordination.Publication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jnr.ffi.Struct.int16_t;
import messenger.proj.models.ChatRoom;
import messenger.proj.models.ConnectionInfo;
import messenger.proj.models.message;
import messenger.proj.repositories.ChatRoomRepository;
import messenger.proj.repositories.MessageRepositroy;

@Service
@Transactional(readOnly = true)
public class MessageService {

	private final MessageRepositroy messageRep;
	private final MessageRedisService messageRedisServ;
	private final RedisTemplate<String, message> redisTemplate;
	private final RedisTemplate<String, String> redisTemplate1;
	private final ConnectionService connectionService;
	private final ChatRoomService chatRoomService;

	@Autowired
	public MessageService(MessageRepositroy messageRep, ChatRoomService chatRoomService,
			ConnectionService connectionService, MessageRedisService messageRedisServ,
			RedisTemplate<String, message> redisTemplate, RedisTemplate<String, String> redisTemplate1) {

		this.chatRoomService = chatRoomService;
		this.messageRep = messageRep;
		this.connectionService = connectionService;
		this.redisTemplate1 = redisTemplate1;
		this.redisTemplate = redisTemplate;
		this.messageRedisServ = messageRedisServ;
	}

	public List<message> findAll() {
		return messageRep.findAll();
	}

	public Optional<message> findById(String messageId) {
		return messageRep.findById(messageId);
	}

	@Transactional
	public void save(message message, String chatId, String currentUserId) {
		
		String id = UUID.randomUUID().toString();
		Optional<ChatRoom> chat = chatRoomService.findById(chatId);
		
		System.out.println();

		if (chat.isPresent()) {

			message.setId(id);
			message.setChatId(chatId);
			
			if (chat.get().getSenderId().equals(currentUserId)) {
				
				message.setSenderId(currentUserId);
				message.setSenderName(chat.get().getSenderName());
				message.setRecipientId(chat.get().getRecipientId());
				message.setRecipientName(chat.get().getRecipientName());
			
			} else {
			
				message.setSenderId(chat.get().getRecipientId());
				message.setSenderName(chat.get().getRecipientName());
				message.setRecipientId(chat.get().getSenderId());
				message.setRecipientName(chat.get().getSenderName());
			
			}
				
			message.setStatus("Unread");
			message.setSendTime(LocalDateTime.now());
			
			messageRedisServ.cacheMessage(id, chatId, message);

			increaseUnreadMessageCounter(message);
		}
	
	}

	@Transactional
	public void increaseUnreadMessageCounter(message message) {

		Optional<ChatRoom> chat = chatRoomService.findById(message.getChatId());

		if (chat.isPresent()) {

			if (message != null && message.getStatus().equals("Unread")
					&& message.getSenderId().equals(chat.get().getSenderId())) {

				// Увеличиваем счетчик для unreadRecipientMessages
				chat.get().setUnreadRecipientMessages(chat.get().getUnreadRecipientMessages() + 1);
				chatRoomService.edit(chat.get());

			} else if (message.getStatus().equals("Unread")
					&& message.getRecipientId().equals(chat.get().getSenderId())) {

				// Увеличиваем счетчик для unreadSenderMessages
				chat.get().setUnreadSenderMessages(chat.get().getUnreadSenderMessages() + 1);
				chatRoomService.edit(chat.get());

			}

		}

	}

	@Transactional
	public void decreaseUnreadMessageCounter(message message) {

		Optional<ChatRoom> chat = chatRoomService.findById(message.getChatId());

		if (chat.isPresent()) {

			if (message != null && message.getStatus().equals("Unread")
					&& message.getSenderId().equals(chat.get().getSenderId())) {

				// Увеличиваем счетчик для unreadRecipientMessages
				chat.get().setUnreadRecipientMessages(chat.get().getUnreadRecipientMessages() - 1);
				chatRoomService.edit(chat.get());

			} else if (message.getStatus().equals("Unread")
					&& message.getRecipientId().equals(chat.get().getSenderId())) {

				// Увеличиваем счетчик для unreadSenderMessages
				chat.get().setUnreadSenderMessages(chat.get().getUnreadSenderMessages() - 1);
				chatRoomService.edit(chat.get());

			}

		}

	}

	public List<message> findByChatId(String chatId) {
		return Stream.concat(getCassandraMessages(chatId).stream(), getCaсhedMessages(chatId).stream())
				.collect(Collectors.toList());
	}

	@Transactional
	public void deleteById(message message) {

		messageRep.deleteById(message.getId());
		redisTemplate.delete("message:" + message.getChatId() + ":" + message.getId());
		redisTemplate1.opsForList().remove(message.getChatId(), 0,
				"message:" + message.getChatId() + ":" + message.getId());

		decreaseUnreadMessageCounter(message);

	}

	public void deleteAllMessagesFromChat(String chatId) {
		findByChatId(chatId).forEach(x -> deleteById(x));
		getCaсhedMessages(chatId).forEach(x -> deleteById(x));

		Optional<ChatRoom> chat = chatRoomService.findById(chatId);

		if (chat.isPresent()) {
			chat.get().setUnreadRecipientMessages(0);
			chat.get().setUnreadSenderMessages(0);
			chatRoomService.edit(chat.get());
		}

	}

	public void readMessages(String curentUserId, ChatRoom chat, HttpServletRequest request) {

		boolean flag = false;

		List<message> messages = findByChatId(chat.getId());
		int end = messages.size() - 1;

		while (end >= 0 && messages.get(end).getStatus().equals("Unread")) {

			if (messages.get(end).getRecipientId().equals(curentUserId)) {

				messages.get(end).setStatus("Read");
				edit(messages.get(end));
				flag = true;

			}

			end--;
		}

		if (flag) {
			if (chat.getRecipientId().equals(curentUserId)) {
				connectionService.userConnection(curentUserId, connectionService.getUserConnection(curentUserId),
						chat.getSenderId());
			} else {
				connectionService.userConnection(curentUserId, connectionService.getUserConnection(chat.getSenderId()),
						chat.getRecipientId());
			}

		}

		chat.setUnreadRecipientMessages(0);
		chat.setUnreadSenderMessages(0);
		chatRoomService.edit(chat);
	}

	@Transactional
	public void edit(message message) {
		List<message> messages = getCaсhedMessages(message.getChatId());
		if (messages.contains(message)) {
			redisTemplate.opsForValue().set("message:" + message.getChatId() + ":" + message.getId(), message);
		} else {
			messageRep.save(message);
		}
	}

	public List<message> getCaсhedMessages(String chatId) {
		return messageRedisServ.getLatestMessages(chatId);
	}

	public List<message> getCassandraMessages(String chatId) {
		return messageRep.findByChatId(chatId);
	}

	public Map<String, message> getLastMessage(List<ChatRoom> chats) {

		HashMap<String, message> lastMessages = new HashMap<>();

		for (ChatRoom chatRoom : chats) {
			try {
				// Получаю последние кэшированные сообщения
				List<message> messages = getCaсhedMessages(chatRoom.getId());
				// Если последних кэшированных сообщений нет, то получаем сообщения из cassandra
				if (messages.isEmpty()) {
					List<message> cassandraMessages = getCassandraMessages(chatRoom.getId());
					lastMessages.put(chatRoom.getId(), cassandraMessages.get(cassandraMessages.size() - 1));
					// Кладем сообщения в hashMap для дальнейшего использования из redis'а, если они
					// есть
				} else {
					lastMessages.put(chatRoom.getId(), messages.get(messages.size() - 1));
				}

			} catch (IndexOutOfBoundsException e) {

			}
		}

		if (lastMessages.size() != 0) {

			List<Map.Entry<String, message>> list = new ArrayList<>(lastMessages.entrySet());

			Collections.sort(list, Comparator.comparing(entry -> entry.getValue().getSendTime()));
			Collections.reverse(list);

			Map<String, message> sortedHashMap = new LinkedHashMap<>();

			for (Map.Entry<String, message> entry : list) {
				sortedHashMap.put(entry.getKey(), entry.getValue());
			}

			return sortedHashMap;
		}

		return lastMessages;

	}

}

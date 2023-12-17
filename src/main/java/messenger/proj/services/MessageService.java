package messenger.proj.services;

import java.time.LocalDateTime;
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

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import messenger.proj.models.ChatRoom;
import messenger.proj.models.message;
import messenger.proj.repositories.MessageRepositroy;

@Service
@Transactional(readOnly = true)
public class MessageService {

	private final MessageRepositroy messageRep;
	private final MessageRedisService messageRedisServ;
	private final RedisTemplate<String, message> redisTemplate;
	private final RedisTemplate<String, String> redisTemplate1;
	private final ChatRoomService chatRoomService;

	@Autowired
	public MessageService(MessageRepositroy messageRep, ChatRoomService chatRoomService, MessageRedisService messageRedisServ,
			RedisTemplate<String, message> redisTemplate, RedisTemplate<String, String> redisTemplate1) {
		this.messageRep = messageRep;
		this.chatRoomService = chatRoomService;
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
	public void save(String chatId, message message) {
		String id = UUID.randomUUID().toString();
		message.setId(id);
		messageRedisServ.cacheMessage(id, chatId, message);
	}

	public List<message> findByChatId(String chatId) {
		return messageRep.findByChatId(chatId);
	}

	@Transactional
	public void deleteById(String messageId, LocalDateTime localDateTime, String chatId) {

		messageRep.deleteByChatId(chatId, localDateTime, messageId);

		redisTemplate.delete("message:" + chatId + ":" + messageId);
		redisTemplate1.opsForList().remove(chatId, 0, "message:" + chatId + ":" + messageId);

	}
	
	// Метод, который устанавливает статус 'Unread' для сообщений и считает их 
	public void setMessageStatus(ChatRoom chat, String chatId) {

		for (message message : messageRedisServ.getLatestMessages(chatId)) {
			if (message.getStatus().equals("Unread") && message.getSenderId().equals(chat.getSenderId())) {
				// Увеличиваем счетчик для unreadRecipientMessages
				chat.setUnreadRecipientMessages(chat.getUnreadRecipientMessages() + 1);
				chatRoomService.chatUnreadMessagesUpdate(chat);
			} else if (message.getStatus().equals("Unread")
					&& message.getRecipientId().equals(chat.getSenderId())) {
				// Увеличиваем счетчик для unreadSenderMessages
				chat.setUnreadSenderMessages(chat.getUnreadSenderMessages() + 1);
				chatRoomService.chatUnreadMessagesUpdate(chat);
			}
		}

		for (message message : findByChatId(chatId)) {
			if (message.getStatus().equals("Unread") && message.getSenderId().equals(chat.getSenderId())) {
				// Увеличиваем счетчик для unreadRecipientMessages
				chat.setUnreadRecipientMessages(chat.getUnreadRecipientMessages() + 1);
				chatRoomService.chatUnreadMessagesUpdate(chat);
			} else if (message.getStatus().equals("Unread")
					&& message.getRecipientId().equals(chat.getSenderId())) {
				// Увеличиваем счетчик для unreadSenderMessages
				chat.setUnreadSenderMessages(chat.getUnreadSenderMessages() + 1);
				chatRoomService.chatUnreadMessagesUpdate(chat);
			}
		}

	}

	@Transactional
	public void edit(message message, String messageId) {
		List<message> messages = getCaсhedMessages(message.getChatId());

		if (messages.contains(message)) {
			redisTemplate.opsForValue().set("message:" + message.getChatId() + ":" + messageId, message);
		} else {
			messageRep.save(message);
		}

	}

	public List<message> getCaсhedMessages(String chatId) {
		return messageRedisServ.getLatestMessages(chatId);

	}

	public Map<String, message> getLastMessage(List<ChatRoom> chats) {

		HashMap<String, message> lastMessages = new HashMap<>();

		for (ChatRoom chatRoom : chats) {
			try {
				List<message> messages = getCaсhedMessages(chatRoom.getId());
				lastMessages.put(chatRoom.getId(), messages.get(messages.size() - 1));
			} catch (IndexOutOfBoundsException e) {
				/* lastMessages.put(chatRoom.getId(), new message()); */
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

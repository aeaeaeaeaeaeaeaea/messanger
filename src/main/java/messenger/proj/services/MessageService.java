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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import messenger.proj.models.ChatRoom;
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
	private final ChatRoomRepository chatRoomRepository;

	@Autowired
	public MessageService(MessageRepositroy messageRep, ChatRoomRepository chatRoomRepository,
			MessageRedisService messageRedisServ, RedisTemplate<String, message> redisTemplate,
			RedisTemplate<String, String> redisTemplate1) {
		this.messageRep = messageRep;
		this.chatRoomRepository = chatRoomRepository;
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

	public void readMessages(String chatId, String curentUserId, ChatRoom chat) {
		boolean flag = false;

		for (message message : getCaсhedMessages(chatId)) {
			if (message.getStatus().equals("Unread") && message.getRecipientId().equals(curentUserId)) {
				message.setStatus("Read");
				flag = true;
				edit(message, message.getId());
			}
		}

		for (message message : findByChatId(chatId)) {
			if (message.getStatus().equals("Unread") && message.getRecipientId().equals(curentUserId)) {
				message.setStatus("Read");
				flag = true;
				edit(message, message.getId());
			}
		}

		chat.setUnreadRecipientMessages(0);
		chat.setUnreadSenderMessages(0);
		chatRoomRepository.save(chat);

	}

	// Метод, который считает сообщения со статусом 'Unread'
	public void setMessageStatus(ChatRoom chat, String chatId) {

		for (message message : messageRedisServ.getLatestMessages(chatId)) {
			if (message.getStatus().equals("Unread") && message.getSenderId().equals(chat.getSenderId())) {
				// Увеличиваем счетчик для unreadRecipientMessages
				chat.setUnreadRecipientMessages(chat.getUnreadRecipientMessages() + 1);
				chatRoomRepository.save(chat);
			} else if (message.getStatus().equals("Unread") && message.getRecipientId().equals(chat.getSenderId())) {
				// Увеличиваем счетчик для unreadSenderMessages
				chat.setUnreadSenderMessages(chat.getUnreadSenderMessages() + 1);
				chatRoomRepository.save(chat);
			}
		}

		for (message message : findByChatId(chatId)) {
			if (message.getStatus().equals("Unread") && message.getSenderId().equals(chat.getSenderId())) {
				// Увеличиваем счетчик для unreadRecipientMessages
				chat.setUnreadRecipientMessages(chat.getUnreadRecipientMessages() + 1);
				chatRoomRepository.save(chat);
			} else if (message.getStatus().equals("Unread") && message.getRecipientId().equals(chat.getSenderId())) {
				// Увеличиваем счетчик для unreadSenderMessages
				chat.setUnreadSenderMessages(chat.getUnreadSenderMessages() + 1);
				chatRoomRepository.save(chat);
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

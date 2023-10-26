package messenger.proj.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

	@Autowired
	public MessageService(MessageRepositroy messageRep, MessageRedisService messageRedisServ,
			RedisTemplate<String, message> redisTemplate, RedisTemplate<String, String> redisTemplate1) {
		this.messageRep = messageRep;
		this.redisTemplate1 = redisTemplate1;
		this.redisTemplate = redisTemplate;
		this.messageRedisServ = messageRedisServ;
	}

	public List<message> findAll() {
		return messageRep.findAll();
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
	public void deleteById(String messageId, String chatId) {

		Optional<message> message = messageRep.findById(messageId);
		
		System.out.println(message.isPresent());

		/* System.out.println("Send time: " + message.getSendTime()); */

		System.out.println("Message id: " + messageId);
		System.out.println("Chat id: " + chatId);

		/* messageRep.deleteByChatId(chatId, messageId); */

		/*
		 * redisTemplate.delete("message:" + chatId + ":" + messageId);
		 * redisTemplate1.opsForList().remove(chatId, 0, "message:" + chatId + ":" +
		 * messageId);
		 */
	}

	@Transactional
	public void edit(message message, String messageId) {
		message.setId(messageId);
		messageRep.save(message);
	}

	/*
	 * @Transactional public void deleteByChatId(String chatId, String messageId) {
	 * messageRep.deleteByChatId(chatId); }
	 */

	public List<message> getCaсhedMessages(String chatId) {
		return messageRedisServ.getLatestMessages(chatId);

	}

	public HashMap<String, message> getLastMessage(List<ChatRoom> chats) {

		HashMap<String, message> lastMessages = new HashMap<>();

		for (ChatRoom chatRoom : chats) {
			try {
				List<message> messages = getCaсhedMessages(chatRoom.getId());
				lastMessages.put(chatRoom.getId(), messages.get(messages.size() - 1));
			} catch (IndexOutOfBoundsException e) {
				lastMessages.put(chatRoom.getId(), new message());
			}
		}

		return lastMessages;

	}

}

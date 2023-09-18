package messenger.proj.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import messenger.proj.models.message;
import messenger.proj.repositories.MessageRepositroy;

@Service
@Transactional(readOnly = true)
public class MessageService {

	private final MessageRepositroy messageRep;
	private final MessageRedisService messageRedisServ;
	private final RedisTemplate<String, message> redisTemplate;

	@Autowired
	public MessageService(MessageRepositroy messageRep, MessageRedisService messageRedisServ,
			RedisTemplate<String, message> redisTemplate) {
		this.messageRep = messageRep;
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
		messageRep.save(message);
		messageRedisServ.cacheMessage(id, chatId, message);
	}

	public List<message> findByChatId(String chatId) {
		return messageRep.findByChatId(chatId);
	}

	@Transactional
	public void deleteById(String messageId) {
		messageRep.deleteById(messageId);
	}

	@Transactional
	public void edit(message message, String messageId) {
		message.setId(messageId);
		messageRep.save(message);
	}

	@Transactional
	public void deleteByChatId(String chatId) {
		messageRep.deleteByChatId(chatId);
	}

	public List<message> getCaсhedMessages(String chatId) {
		return messageRedisServ.getLatestMessages(chatId);

	}

	public List<message> getAllMessage(String chatId) {

		Set<String> keySet = redisTemplate.keys(chatId + ":*");

		List<message> redisMessages = new ArrayList<message>();
		for (String kString : keySet) {
			redisMessages.add(redisTemplate.opsForValue().get(kString));
		}
		
		List<message> cassandraMessages = messageRep.findByChatId(chatId);
		
        List<message> combinedMessages = new ArrayList<>();
        combinedMessages.addAll(redisMessages);
        combinedMessages.addAll(cassandraMessages);
        
        Set<message> uniqueMessages = new HashSet<>(combinedMessages);
        
        for (message m : uniqueMessages) {
        	System.out.println("MESSAGE: " + m.getId());
        }
        
        return new ArrayList<>(uniqueMessages);
	}

}

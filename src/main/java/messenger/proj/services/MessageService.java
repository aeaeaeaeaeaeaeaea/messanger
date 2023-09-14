package messenger.proj.services;

import java.util.ArrayList;
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
	public MessageService(MessageRepositroy messageRep, MessageRedisService messageRedisServ, RedisTemplate<String, message> redisTemplate) {
		this.messageRep = messageRep;
		this.redisTemplate = redisTemplate;
		this.messageRedisServ = messageRedisServ;
	}

	public List<message> findAll() {
		return messageRep.findAll();
	}

	@Transactional
	public void save(message message) {
		String id = UUID.randomUUID().toString();
		message.setId(id);
		messageRep.save(message);
		messageRedisServ.cacheMessage(id, message);
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
	
	public List<message> getCaсhedMessages() {
		
	
//        List<message> recentMessages = new ArrayList<>();
//        List<message> recentMessageIds = redisTemplate.opsForList().range(MessageRedisService.RECENT_MESSAGES_KEY, 0, MessageRedisService.MAX_RECENT_MESSAGES - 1);
//        
//        for (message message : recentMessageIds) {
//            if (message != null) {
//                recentMessages.add(message);
//            }
//        }
//        
        return null;
		 
	}
	
	
}

package messenger.proj.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import messenger.proj.models.message;
import messenger.proj.repositories.MessageRepositroy;

@Service
public class MessageRedisService {

	private final RedisTemplate<String, message> redisTemplate;
	private final MessageRepositroy messageRep;
	private final RedisTemplate<String, String> redisTemplate1;
	
	@Autowired
	public MessageRedisService(MessageRepositroy messageRep, 
							   RedisTemplate<String, message> redisTemplate,
							   RedisTemplate<String, String> redisTemplate1
							  ) {
		this.redisTemplate = redisTemplate;
		this.redisTemplate1 = redisTemplate1;
		this.messageRep = messageRep;
	}

	public void cacheMessage(String messageId, String chatId, message message) {
		
		//Set<String> keySet = redisTemplate.keys("message:" + chatId+":*");
		List<String> list = redisTemplate1.opsForList().range(chatId, 0, -1);
		
		
		if (list.size() >= 5) {
			messageRep.save(redisTemplate.opsForValue().get(list.get(0)));
			
			redisTemplate.delete(list.get(0));
			redisTemplate1.opsForList().remove(chatId, 0, list.get(0));
			
			redisTemplate.opsForValue().set("message:" + chatId + ":" + messageId, message);
			redisTemplate1.opsForList().rightPush(chatId, "message:" + chatId + ":" + messageId);
			
		} else {
			redisTemplate1.opsForList().rightPush(chatId, "message:" + chatId + ":" + messageId);
			redisTemplate.opsForValue().set("message:" + chatId + ":" + messageId, message);
		}
		
    }

    public List<message> getLatestMessages(String chatId) {
    	
    	
    	List<String> list = redisTemplate1.opsForList().range(chatId, 0, -1);
    	
    	List<message> messages = new ArrayList<message>();
    	for (String kString : list) {
    		messages.add(redisTemplate.opsForValue().get(kString));
    	}

    	
    	return messages;
        
    }

}

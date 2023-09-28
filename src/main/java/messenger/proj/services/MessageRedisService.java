package messenger.proj.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import messenger.proj.models.message;
import messenger.proj.repositories.MessageRepositroy;

@Service
public class MessageRedisService {

	private final RedisTemplate<String, message> redisTemplate;
	private final MessageRepositroy messageRep;

	public MessageRedisService(MessageRepositroy messageRep, RedisTemplate<String, message> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.messageRep = messageRep;
	}

	public void cacheMessage(String messageId, String chatId, message message) {
		
		Set<String> keySet = redisTemplate.keys(chatId+":*");
		String[] arrayList =  keySet.toArray(new String[0]);
		
		if (keySet.size() >= 5) {
			messageRep.save(redisTemplate.opsForValue().get(arrayList[arrayList.length - 1]));
			redisTemplate.delete(arrayList[arrayList.length - 1]);
			redisTemplate.opsForValue().set("message:" + chatId + ":" + messageId, message);
		} else {
			redisTemplate.opsForValue().set("message:" + chatId + ":" + messageId, message);
		}
		
        
    }

    public List<message> getLatestMessages(String chatId) {
    	
    	Set<String> keySet = redisTemplate.keys("message:" + chatId + ":*");
    	
    	List<message> messages = new ArrayList<message>();
    	for (String kString : keySet) {
    		messages.add(redisTemplate.opsForValue().get(kString));
    	}
    	
    	return messages;
        
    }

}

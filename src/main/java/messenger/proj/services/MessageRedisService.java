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

@Service
public class MessageRedisService {

	private final RedisTemplate<String, message> redisTemplate;

	public MessageRedisService(RedisTemplate<String, message> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void cacheMessage(String messageId, String chatId, message message) {
		
		Set<String> keySet = redisTemplate.keys(chatId+":*");
		String[] arrayList =  keySet.toArray(new String[0]);
		
		if (keySet.size() >= 5) {
			redisTemplate.delete(arrayList[arrayList.length - 1]);
			redisTemplate.opsForValue().set(chatId + ":" + messageId, message);
		} else {
			redisTemplate.opsForValue().set(chatId + ":" + messageId, message);
		}
		
        
    }

    public List<message> getLatestMessages(String chatId) {
    	
    	Set<String> keySet = redisTemplate.keys(chatId + ":*");
    	
    	List<message> messages = new ArrayList<message>();
    	for (String kString : keySet) {
    		messages.add(redisTemplate.opsForValue().get(kString));
    	}
    	
    	return messages;
        
    }

}

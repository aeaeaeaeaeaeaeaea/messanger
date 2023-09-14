package messenger.proj.services;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import messenger.proj.models.message;

@Service
public class MessageRedisService {

	private final RedisTemplate<String, message> redisTemplate;
	static final int MAX_RECENT_MESSAGES = 10;

	public MessageRedisService(RedisTemplate<String, message> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public message getMessageFromCache(String messageId) {
		return (message) redisTemplate.opsForValue().get(messageId);
	}

	public void cacheMessage(String messageId, message message) {

		redisTemplate.opsForSet().add(messageId, message);

		long currentKeyCount = redisTemplate.opsForSet().size(messageId);

		if (currentKeyCount > MAX_RECENT_MESSAGES) {
			long keysToRemove = currentKeyCount - MAX_RECENT_MESSAGES;
			Set<String> keysToRemoveSet = redisTemplate.opsForSet().pop(messageId, keysToRemove);

			
			for (String keyToRemove : keysToRemoveSet) {
				redisTemplate.delete(keyToRemove);
			}
		}
	}

}

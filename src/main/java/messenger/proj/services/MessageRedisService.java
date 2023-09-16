package messenger.proj.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import messenger.proj.models.message;

@Service
public class MessageRedisService {

	private final RedisTemplate<String, message> redisTemplate;
	static final String MESSAGE_KEY = "latest_messages";
	static final int MAX_RECENT_MESSAGES = 10;
	

	public MessageRedisService(RedisTemplate<String, message> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void cacheMessage(message message) {
        redisTemplate.opsForList().leftPush(MESSAGE_KEY, message);
    }

    public List<message> getLatestMessages(int count) {
        return redisTemplate.opsForList().range(MESSAGE_KEY, 0, count - 1);
    }

}

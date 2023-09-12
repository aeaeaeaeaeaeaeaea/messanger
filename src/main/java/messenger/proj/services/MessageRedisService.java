package messenger.proj.services;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import messenger.proj.models.message;

@Service
public class MessageRedisService {

    private final RedisTemplate<String, message> redisTemplate;

    public MessageRedisService(RedisTemplate<String, message> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public message getMessageFromCache(String messageId) {
        return (message) redisTemplate.opsForValue().get(messageId);
    }

    public void cacheMessage(String messageId, message message) {
        redisTemplate.opsForValue().set(messageId, message);
    }

    
}

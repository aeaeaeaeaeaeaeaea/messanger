package messenger.proj.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import messenger.proj.models.Message;
import messenger.proj.repositories.MessageRepositroy;

@Service
public class MessageRedisService {

	private final RedisTemplate<String, Message> redisTemplate;
	private final MessageRepositroy messageRep;
	private final RedisTemplate<String, String> redisTemplate1;

	@Autowired
	public MessageRedisService(MessageRepositroy messageRep, RedisTemplate<String, Message> redisTemplate,
			RedisTemplate<String, String> redisTemplate1) {
		this.redisTemplate = redisTemplate;
		this.redisTemplate1 = redisTemplate1;
		this.messageRep = messageRep;
	}
	
	public Message getMessageById(String messageId, String chatId) {
		return redisTemplate.opsForValue().get("message:" + chatId + ":" + messageId);
	}

	public void cacheMessage(String messageId, String chatId, Message message) {
		

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

	public List<Message> getLatestMessages(String chatId) {
		// Возврщаем все кэшированны сообщения для каждого чата
		return redisTemplate1.opsForList().range(chatId, 0, -1).stream()
    			.map(x -> redisTemplate.opsForValue().get(x))
    			.collect(Collectors.toList());

	}

}

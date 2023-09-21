package messenger.proj.services;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import messenger.proj.models.User;

@Service
public class ConnectionService {
	
	private final RedisTemplate<String, User> redisTemplate;
	
	@Autowired
	public ConnectionService(RedisTemplate<String, User> redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
	}
	
	public void userConnection(String userId, User user) {
		redisTemplate.opsForValue().set("user:" + userId, user);
	}
	
	
=======

public class ConnectionService {
>>>>>>> origin/master

}

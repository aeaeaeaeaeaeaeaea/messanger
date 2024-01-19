package messenger.proj.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.validator.internal.util.privilegedactions.NewInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import messenger.proj.models.ConnectionInfo;
import messenger.proj.models.User;

@Service
public class ConnectionService {

	private final RedisTemplate<String, ConnectionInfo> redisTemplate;
	private final UserService userServ;

	@Autowired
	public ConnectionService(UserService userServ, RedisTemplate<String, ConnectionInfo> redisTemplate) {
		super();
		this.userServ = userServ;
		this.redisTemplate = redisTemplate;
	}

	public void userConnection(String userId, ConnectionInfo connectionInfo, HttpServletRequest request) {

		User user = userServ.findById(userId).get();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		connectionInfo.setUserName(user.getUsername());
		connectionInfo.setUserId(userId);
		connectionInfo.setIpAddress(request.getRemoteAddr().toString());
		connectionInfo.setLogInTime(LocalDateTime.now().format(formatter).toString());
		connectionInfo.setOnlineStatus("Online");

		redisTemplate.opsForValue().set("user:" + userId, connectionInfo);
	}

	public void setUserOfline(String userId) {
		redisTemplate.delete("user:" + userId);
	}

	public ConnectionInfo getUserConnection(String userId) {
		Set<String> keySet = redisTemplate.keys("user:" + userId);
		ConnectionInfo connectionInfo = null;
		for (String key : keySet) {
			connectionInfo = redisTemplate.opsForValue().get(key);
		}
		return connectionInfo;
	}

}

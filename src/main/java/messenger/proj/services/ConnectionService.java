package messenger.proj.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import messenger.proj.DTO.ConnectionInfoDTO;
import messenger.proj.models.User;
import messenger.proj.security.PersonDetails;

@Service
public class ConnectionService {

	private final RedisTemplate<String, ConnectionInfoDTO> redisTemplate;
	private final UserService userServ;

	@Autowired
	public ConnectionService(UserService userServ, RedisTemplate<String, ConnectionInfoDTO> redisTemplate) {
		super();
		this.userServ = userServ;
		this.redisTemplate = redisTemplate;
	}

	public String getCurrentUserId() {
		// Получаем данные о текущем пользователе
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
		String currentUserId = personDetails.getUser().getId();

		return currentUserId;
	}

	public void userConnection(String userId, ConnectionInfoDTO connectionInfo, String recipientId) {

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		ConnectionInfoDTO recipientUserConnectionInfo = getUserConnection(recipientId);
		
		if (connectionInfo != null && recipientUserConnectionInfo != null && connectionInfo.getCurrentPage().substring(6)
				.equals(recipientUserConnectionInfo.getCurrentPage().substring(6))) {

			recipientUserConnectionInfo.setOnlineStatus("Online");
			redisTemplate.opsForValue().set("user:" + recipientId, recipientUserConnectionInfo);

		
			Runnable task = () -> recipientUserConnectionInfo.setOnlineStatus(null);
			Runnable task2 = () -> redisTemplate.opsForValue().set("user:" + recipientId, recipientUserConnectionInfo);

			scheduler.schedule(task, 10, TimeUnit.SECONDS);
			scheduler.schedule(task2, 11, TimeUnit.SECONDS);
		}

		
		User user = userServ.findById(userId).get();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		connectionInfo.setUserName(user.getUsername());
		connectionInfo.setUserId(userId);
		connectionInfo.setLogInTime(LocalDateTime.now().format(formatter).toString());
		connectionInfo.setOnlineStatus("Online");
		
		
		Runnable task = () -> connectionInfo.setOnlineStatus(null);
		Runnable task2 = () -> redisTemplate.opsForValue().set("user:" + userId, connectionInfo);

		scheduler.schedule(task, 10, TimeUnit.SECONDS);
		scheduler.schedule(task2, 11, TimeUnit.SECONDS);

		redisTemplate.opsForValue().set("user:" + userId, connectionInfo);
		
	}

	public void setCurrentPage(String userId, ConnectionInfoDTO connectionInfo, String currentPage) {
		
		
		if (connectionInfo != null) {
			
			connectionInfo.setCurrentPage(currentPage);
			redisTemplate.opsForValue().set("user:" + userId, connectionInfo);
			
		} else {
			User user = userServ.findById(userId).get();
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			ConnectionInfoDTO connectionInfo2 = new ConnectionInfoDTO();
			
			connectionInfo2.setUserName(user.getUsername());
			connectionInfo2.setUserId(userId);
			connectionInfo2.setLogInTime(LocalDateTime.now().format(formatter).toString());
			connectionInfo2.setCurrentPage(currentPage);
			
			redisTemplate.opsForValue().set("user:" + userId, connectionInfo2);
		}
	}

	public void setUserOffline(String userId) {
		redisTemplate.delete("user:" + userId);
	}

	public ConnectionInfoDTO getUserConnection(String userId) {
		Set<String> keySet = redisTemplate.keys("user:" + userId);
		ConnectionInfoDTO connectionInfo = null;
		for (String key : keySet) {
			connectionInfo = redisTemplate.opsForValue().get(key);
		}
		return connectionInfo;
	}

}

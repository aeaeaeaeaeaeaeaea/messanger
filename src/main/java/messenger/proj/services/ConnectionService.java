package messenger.proj.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.index.mapper.StringFieldType;
import org.hibernate.validator.internal.util.privilegedactions.NewInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import messenger.proj.models.ConnectionInfo;
import messenger.proj.models.User;
import messenger.proj.security.PersonDetails;

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

	public String getCurrentUserId() {
		// Получаем данные о текущем пользователе
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
		String currentUserId = personDetails.getUser().getId();

		return currentUserId;
	}

	public void userConnection(String userId, ConnectionInfo connectionInfo, String recipientId) {

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		ConnectionInfo recipientUserConnectionInfo = getUserConnection(recipientId);

		if (connectionInfo != null && connectionInfo.getCurrentPage().substring(6)
				.equals(recipientUserConnectionInfo.getCurrentPage().substring(6))) {

			recipientUserConnectionInfo.setOnlineStatus("Online");
			redisTemplate.opsForValue().set("user:" + recipientId, recipientUserConnectionInfo);

			// Определенный метод, который вы хотите вызвать через 10 секунд
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

		redisTemplate.opsForValue().set("user:" + userId, connectionInfo);
	}

	public void setCurrentPageForUserConnection(String userId, ConnectionInfo connectionInfo, String currentPage) {
		
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		if (connectionInfo != null) {
			connectionInfo.setCurrentPage(currentPage);
			redisTemplate.opsForValue().set("user:" + userId, connectionInfo);
		} else {
			User user = userServ.findById(userId).get();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			ConnectionInfo connectionInfo2 = new ConnectionInfo();
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

	public ConnectionInfo getUserConnection(String userId) {
		Set<String> keySet = redisTemplate.keys("user:" + userId);
		ConnectionInfo connectionInfo = null;
		for (String key : keySet) {
			connectionInfo = redisTemplate.opsForValue().get(key);
		}
		return connectionInfo;
	}

}

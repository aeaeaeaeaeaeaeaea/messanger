package messenger.proj.models;

import java.time.LocalDateTime;

public class ConnectionInfo {
	
	private String userName;
	private String userId;
	private String logInTime;
	private String onlineStatus;
	
	public ConnectionInfo() {
		
	}
	
	public ConnectionInfo(String userName, String onlineStatus, String userId, String logInTime) {
		super();
		this.onlineStatus = onlineStatus;
		this.userName = userName;
		this.userId = userId;
		this.logInTime = logInTime;
	}

	public String getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLogInTime() {
		return logInTime;
	}

	public void setLogInTime(String logInTime) {
		this.logInTime = logInTime;
	}
	
	
	
	

}

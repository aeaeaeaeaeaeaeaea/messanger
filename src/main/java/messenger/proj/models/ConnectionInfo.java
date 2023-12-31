package messenger.proj.models;

import java.time.LocalDateTime;

public class ConnectionInfo {
	
	private String userName;
	private String userId;
	private String ipAddress;
	private String logInTime;
	
	public ConnectionInfo() {
		
	}
	
	public ConnectionInfo(String userName, String userId, String ipAddress, String logInTime) {
		super();
		this.userName = userName;
		this.userId = userId;
		this.ipAddress = ipAddress;
		this.logInTime = logInTime;
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

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getLogInTime() {
		return logInTime;
	}

	public void setLogInTime(String logInTime) {
		this.logInTime = logInTime;
	}
	
	
	
	

}

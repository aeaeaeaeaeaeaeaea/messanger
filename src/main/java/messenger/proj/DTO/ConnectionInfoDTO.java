package messenger.proj.DTO;

public class ConnectionInfoDTO {
	
	private String userName;
	private String userId;
	private String logInTime;
	private String onlineStatus;
	private String currentPage;
	
	public ConnectionInfoDTO() {
		
	}
	
	public ConnectionInfoDTO(String userName, String currentPage, String onlineStatus, String userId, String logInTime) {
		super();
		this.onlineStatus = onlineStatus;
		this.currentPage = currentPage;
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
	

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	public String toString() {
		return "ConnectionInfo [userName=" + userName + ", userId=" + userId + ", logInTime=" + logInTime
				+ ", onlineStatus=" + onlineStatus + ", currentPage=" + currentPage + "]";
	}

}

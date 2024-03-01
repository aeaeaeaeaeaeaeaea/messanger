package messenger.proj.DTO;

public class MessageDTO {
	
	private String id;
	private String senderName;
	private String content;
	private String status;
	private String chatId;
	
	public MessageDTO() {
		
	}

	public MessageDTO(String chatId, String id, String senderName, String content, String status) {
		super();
		this.id = id;
		this.senderName = senderName;
		this.content = content;
		this.status = status;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	@Override
	public String toString() {
		return "MessageDTO [senderName=" + senderName + ", content=" + content + ", status=" + status + "]";
	}
	
	
	
	
	

}

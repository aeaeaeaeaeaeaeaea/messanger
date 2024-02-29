package messenger.proj.DTO;

public class MessageDTO {
	
	private String senderName;
	private String content;
	private String status;
	
	public MessageDTO() {
		
	}

	public MessageDTO(String senderName, String content, String status) {
		super();
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
	
	
	
	
	

}

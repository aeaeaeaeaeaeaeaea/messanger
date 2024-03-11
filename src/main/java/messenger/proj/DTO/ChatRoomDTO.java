package messenger.proj.DTO;

public class ChatRoomDTO {
	
	private String id;
	private String senderId;
	private String recipientId;
	
	
	public ChatRoomDTO(String id, String senderId, String recipientId) {
		this.recipientId = recipientId;
		this.senderId = senderId;
		this.id = id;
	}


	public ChatRoomDTO() {
		
	}
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getSenderId() {
		return senderId;
	}


	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}


	public String getRecipientId() {
		return recipientId;
	}


	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}


	@Override
	public String toString() {
		return "ChatRoomDTO [senderId=" + senderId + ", recipientId=" + recipientId + "]";
	}
	
	
	
}

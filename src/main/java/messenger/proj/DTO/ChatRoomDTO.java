package messenger.proj.DTO;

public class ChatRoomDTO {
	
	private String id;
	private String senderId;
	private String recipientName;
	private String senderName;
	private String recipientId;
	private String unreadRecipientMessagesString;
	private String unreadSenderMessages;
	
	public ChatRoomDTO(String id, String senderId, String senderName, String recipientName, String recipientId, String unreadRecipientMessagesString, String unreadSenderMessages) {
		super();
		this.id = id;
		this.senderName = senderName;
		this.recipientName = recipientName;
		this.recipientId = recipientId;
		this.senderId = senderId;
		this.unreadRecipientMessagesString = unreadRecipientMessagesString;
		this.unreadSenderMessages = unreadSenderMessages;
	}


	public ChatRoomDTO() {
		
	}
	
	
	
	
	public String getRecipientName() {
		return recipientName;
	}


	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}


	public String getSenderName() {
		return senderName;
	}


	public void setSenderName(String senderName) {
		this.senderName = senderName;
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


	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUnreadRecipientMessagesString() {
		return unreadRecipientMessagesString;
	}
	
	public void setUnreadRecipientMessagesString(String unreadRecipientMessagesString) {
		this.unreadRecipientMessagesString = unreadRecipientMessagesString;
	}
	
	public String getUnreadSenderMessages() {
		return unreadSenderMessages;
	}
	
	public void setUnreadSenderMessages(String unreadSenderMessages) {
		this.unreadSenderMessages = unreadSenderMessages;
	}
	
	
	
	
}

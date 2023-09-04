package messenger.proj.models;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("chatroom")
public class ChatRoom {
	
	@PrimaryKey
	private String id;
	
	@Column("chatid")
	private String chatId;
	
	@Column("senderid")
	private String senderId;
	
	@Column("recipientid")
	private String recipientId;
	
	public ChatRoom() {
	}

	public ChatRoom(String id, String chatId, String senderId, String recipientId) {
		this.id = id;
		this.chatId = chatId;
		this.senderId = senderId;
		this.recipientId = recipientId;
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
	
	
	
	
	

}

package messenger.proj.models;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("chatroom")
public class ChatRoom {
	
	@PrimaryKey
	private String id;

	
	@Column("recipientname")
	private String recipientName;
	
	@Column("sendername")
	private String senderName;
	
	@Column("senderid")
	private String senderId;
	
	@Column("recipientid")
	private String recipientId;
	
	public ChatRoom() {
	}

	public ChatRoom(String id, String senderId, String senderName, String recipientName, String recipientId) {
		this.id = id;
		this.senderName = senderName;
		this.recipientName = recipientName;
		this.senderId = senderId;
		this.recipientId = recipientId;
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

}

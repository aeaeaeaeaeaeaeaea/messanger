package messenger.proj.models;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("ChatNotification")
public class ChatNotiflication {
	
	@PrimaryKey
	private String id;
	
	@Column("senderId")
	private String senderId;
	
	@Column("senderName")
	private String senderName;
	
	public ChatNotiflication() {
		
	}

	public ChatNotiflication(String id, String senderId, String senderName) {
		this.id = id;
		this.senderId = senderId;
		this.senderName = senderName;
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

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	
	
	
	
	
}

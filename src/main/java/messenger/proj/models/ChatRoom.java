package messenger.proj.models;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.cluster.metadata.AliasAction.NewAliasValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import jnr.ffi.Struct.int16_t;
import messenger.proj.repositories.MessageRepositroy;
import messenger.proj.services.MessageService;

@Table("chatroom")
@Component
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
	
	@Column("unreadrecipientmessages")
	private int unreadRecipientMessages;
	
	@Column("unreadsendermessages")
	private int unreadSenderMessages;
	

	public ChatRoom(String id, int unreadSenderMessages,  int unreadRecipientMessages, String senderId, String senderName, String recipientName, String recipientId) {
		this.id = id;
		this.unreadRecipientMessages = unreadRecipientMessages;
		this.senderName = senderName;
		this.recipientName = recipientName;
		this.senderId = senderId;
		this.recipientId = recipientId;
	}

	public ChatRoom() {
		
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

	public int getUnreadRecipientMessages() {
		return unreadRecipientMessages;
	}

	public void setUnreadRecipientMessages(int unreadRecipientMessages) {
		this.unreadRecipientMessages = unreadRecipientMessages;
	}

	public int getUnreadSenderMessages() {
		return unreadSenderMessages;
	}

	public void setUnreadSenderMessages(int unreadSenderMessages) {
		this.unreadSenderMessages = unreadSenderMessages;
	}

	
	
	

}

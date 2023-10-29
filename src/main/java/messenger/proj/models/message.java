package messenger.proj.models;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Table("messages")
@JsonIgnoreProperties(ignoreUnknown = true)
public class message {
	
	@PrimaryKey
	private String id;
	
	@Column("chatid")
	private String chatId;
	
	@Column("senderid")
	private String senderId;
	
	@Column("recipientid")
	private String recipientId;
	
	@Column("sendername")
	private String senderName;
	
	@Column("recipientname")
	private String recipientName;
	
	@Column("content")
	private String content;
	
	@Column("status")
	private String status;
	
	@Column("sendtime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime sendTime;

	public message() {	
	}


	public message(String id, LocalDateTime sendTime, String chatId, String senderId, String recipientId, String senderName,
			String recipientName, String content, String status) {
		super();
		this.id = id;
		this.sendTime = sendTime;
		this.chatId = chatId;
		this.senderId = senderId;
		this.recipientId = recipientId;
		this.senderName = senderName;
		this.recipientName = recipientName;
		this.content = content;
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


	public String getSenderName() {
		return senderName;
	}


	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}


	public String getRecipientName() {
		return recipientName;
	}


	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
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
	
	public LocalDateTime getSendTime() {
		return sendTime;
	}

	public void setSendTime(LocalDateTime sendTime) {
		this.sendTime = sendTime;
	}


	@Override
	public String toString() {
		return "message [id=" + id + ", chatId=" + chatId + ", senderId=" + senderId + ", recipientId=" + recipientId
				+ ", senderName=" + senderName + ", recipientName=" + recipientName + ", content=" + content
				+ ", status=" + status + ", sendTime=" + sendTime + "]";
	}
	
	
		
}

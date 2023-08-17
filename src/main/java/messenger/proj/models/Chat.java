package messenger.proj.models;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("chat")
public class Chat {
	
	@PrimaryKey
	private UUID id;
	
	@Column("sender")
	private UUID sender;
	
	@Column("recipient")
	private UUID recipient;
	
	public Chat() {
	}

	public Chat(UUID id, UUID sender, UUID recipient) {
		super();
		this.id = id;
		this.sender = sender;
		this.recipient = recipient;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getSender() {
		return sender;
	}

	public void setSender(UUID sender) {
		this.sender = sender;
	}

	public UUID getRecipient() {
		return recipient;
	}

	public void setRecipient(UUID recipient) {
		this.recipient = recipient;
	}
	
	

}

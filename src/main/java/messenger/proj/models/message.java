package messenger.proj.models;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("message1")
public class message {
	
	@PrimaryKey
	private UUID id;
	
	@Column("message")
	private String message;
	
	public message() {
		
	}
	
	public message(UUID id, String message) {
		this.id = id;
		this.message = message;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public UUID getId() {
		return this.id;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
}

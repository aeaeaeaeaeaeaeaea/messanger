package messenger.proj.models;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Table("file")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileEntry {

	@PrimaryKey
	private String id;
	
	@Column("path")
	private String path;
	
	@Column("messageid")
	private String messageId;
	
	public FileEntry() {
		
	}

	public FileEntry(String id, String messageId, String path) {
		this.id = id;
		this.messageId = messageId;
		this.path = path;
	}
	
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "FileEntry [id=" + id + ", path=" + path + ", messageId=" + messageId + "]";
	}
}

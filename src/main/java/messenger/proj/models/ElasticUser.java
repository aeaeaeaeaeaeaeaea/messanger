package messenger.proj.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "user")
public class ElasticUser {
	
	@Id
	private String id;
	
	@Field(type = FieldType.Text, name = "userName")
	private String userName;
	
	public ElasticUser() {
	}
	
	public ElasticUser(String id, String userName) {
		super();
		this.id = id;
		this.userName = userName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}

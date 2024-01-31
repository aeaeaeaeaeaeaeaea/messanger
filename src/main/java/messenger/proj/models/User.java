package messenger.proj.models;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.elasticsearch.annotations.Document;

@Table("users")
public class User {
	
	@PrimaryKey
	private String id;
	
	@Column("password")
	private String password;
	
	@Column("username")
	private String username;
	
	@Column("role")
	private String role;
	
	@Column("phonenumber")
	private String phoneNumber;
	
	public User() {
	}
	
	public User(String role, String phoneNumber, String password, String id, String username) {
		this.id = id;
		this.role = role;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.username = username;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getRole() {
		return this.role;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
 
}

package aii.data;

import aii.logic.UserId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "USERS")
public class UserEntity {
	
	@Id
	private String userId;
	
	@Transient
	private UserRole role;
	
	private String username;
	private String avatar;

	public UserEntity() {

	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public void setUserId(UserId userId) {
		this.userId = userId.getSystemID() + "@@" + userId.getEmail();
	}

}

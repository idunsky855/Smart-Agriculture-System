package aii.logic;

import aii.data.UserEntity;
import aii.data.UserRole;

public class UserBoundary {
	private UserId userId;
	private UserRole role;
	private String username;
	private String avatar;

	public UserBoundary() {

	}

	public UserBoundary(String systemID, String email, UserRole role, String username, String avatar) {
		userId = new UserId(systemID, email);
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}
	
	public UserBoundary (UserEntity entity) {
		this();
		
		String systemID = entity.getUserId().split("@@")[0];
		String email = entity.getUserId().split("@@")[1];
		this.userId = new UserId(systemID, email);
		this.username = entity.getUsername();
		this.avatar = entity.getAvatar();
		
		// TODO initialize other boundary fields using entity
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

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}
	
	public UserEntity toEntity() {
		UserEntity entity = new UserEntity();
		
		entity.setUserId(userId);
		entity.setUsername(username);
		entity.setAvatar(avatar);
		
		// TODO update other field of entity
		
		return entity;
	}

	@Override
	public String toString() {
		return "UserBoundary [userId=" + userId + ", role=" + role + ", username=" + username + ", avatar=" + avatar
				+ "]";
	}

}
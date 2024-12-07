package demo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserBoundary {
	private Map<String, String> userId;
	private String role;
	private String username;
	private String avatar;

	private final String systemID = "2025a.liron.barshishat";
	
	public UserBoundary() {
		userId = new ConcurrentHashMap<>();
		userId.put("systemID", systemID);
		userId.put("email", "");
	}
 
	public UserBoundary(String email, String role, String username, String avatar) {
		userId = new ConcurrentHashMap<>();
		userId.put("systemID", systemID);
		userId.put("email", email);
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}
 
	public Map<String, String> getUserId() {
		return userId;
	}
	
	public String getRole() {
		return role;
	}
 
	public void setRole(String role) {
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

	@Override
	public String toString() {
		return "UserBoundary [userId=" + userId + ", role=" + role + 
				", username=" + username + ", avatar=" + avatar +"]";
	}
	
}
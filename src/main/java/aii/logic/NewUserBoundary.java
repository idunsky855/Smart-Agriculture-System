package aii.logic;

import aii.data.UserRole;

public class NewUserBoundary {
	
	private String email;
	private UserRole role;
	private String username;
	private String avatar;

	public NewUserBoundary() {

	}

	public NewUserBoundary(String email, UserRole role, String username, String avatar) {
		this.email = email;
		this.role = role;
		this.username = username;
		this.avatar = avatar;
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


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "NewUserBoundary [email=" + email + ", role=" + role + ", username=" + username + ", avatar=" + avatar
				+ "]";
	}


}

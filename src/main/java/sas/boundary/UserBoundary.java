package sas.boundary;

public class UserBoundary {
	private UserId userId;
	private RoleEnum role;
	private String username;
	private String avatar;

	public UserBoundary() {

	}

	public UserBoundary(String systemID, String email, RoleEnum role, String username, String avatar) {
		userId = new UserId(systemID, email);
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}

	public RoleEnum getRole() {
		return role;
	}

	public void setRole(RoleEnum role) {
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

	@Override
	public String toString() {
		return "UserBoundary [userId=" + userId + ", role=" + role + ", username=" + username + ", avatar=" + avatar
				+ "]";
	}

}
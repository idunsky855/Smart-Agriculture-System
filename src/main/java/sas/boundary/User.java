package sas.boundary;

import sas.utils.Constants;

public class User {
	private String userId;
	private String email;

	public User(){
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setEmail(String emailAddress){
		this.email = emailAddress;
	}

	public String getEmail(){
		return this.email;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", email=" + email + "]";
	}
	
	

}

package sas.boundary;

import sas.boundary.User;

public class CreatedBy {
	private User userId;
	

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "CreatedBy [ " + userId +" ]";
	}
}

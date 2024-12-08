package sas.boundary;

import sas.utils.Constants;

public class User {
	private String userId;
	private String systemId = Constants.SYSTEM_ID;
	
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", systemId=" + systemId + "]";
	}
	
	

}

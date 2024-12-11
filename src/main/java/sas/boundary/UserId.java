package sas.boundary;

public class UserId {
	private String systemID;
	private String email;
	
	public UserId() {

	}
	
	public UserId(String systemID, String email) {
		this.systemID = systemID;
		this.email = email;
	}
	
	public String getSystemID() {
		return systemID;
	}

	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserId [systemID=" + systemID + ", email=" + email + "]";
	}
	
	
}

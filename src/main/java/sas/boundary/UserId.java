package sas.boundary;

public class UserId {
	private String systemId;
	private String email;

	public UserId(){
	}

	public void setEmail(String emailAddress){
		this.email = emailAddress;
	}

	public String getEmail(){
		return this.email;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	@Override
	public String toString() {
		return "User [email=" + email + " systemId=" + systemId + "]";
	}


}
package aii.logic;

public class UserId {
    private String systemID;
    private String email;

	public UserId() {
		
	}

	public UserId(String systemID, String email) {
		this.systemID = systemID;
		this.email = email;
	}

	public void setEmail(String emailAddress) {
		this.email = emailAddress;
	}

	public String getEmail() {
		return this.email;
	}

    public String getSystemID() {
        return systemID;
    }

    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }

	@Override
	public String toString() {
		return "User [email=" + email + " systemID=" + systemID + "]";
	}


}

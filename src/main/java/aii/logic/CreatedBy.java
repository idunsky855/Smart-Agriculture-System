package aii.logic;

public class CreatedBy {
	private UserId userId;
	
	public CreatedBy(){
	}
	public CreatedBy(UserId userId){
		this.userId = userId;
	}
	public CreatedBy(String systemID,String userEmail){
		this.userId = new UserId(systemID, userEmail);
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "CreatedBy [ " + userId +" ]";
	}
}

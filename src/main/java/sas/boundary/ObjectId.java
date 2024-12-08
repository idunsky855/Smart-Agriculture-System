package sas.boundary;

public class ObjectId {
	private String id;
	private String systemId;
	
	
	public ObjectId() {
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getSystemId() {
		return systemId;
	}


	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}


	@Override
	public String toString() {
		return "ObjectId [id=" + id + ", systemId=" + systemId + "]";
	}
}

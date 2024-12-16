package aii.logic;

import java.util.Objects;

public class ObjectId {
	private String systemID;
	private String id;


	public ObjectId() {
	}

	public ObjectId(String id, String systemID){
		this.id = id;
		this.systemID = systemID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSystemID() {
		return systemID;
	}

	public void setSystemId(String systemID) {
		this.systemID = systemID;
	}

	@Override
	public String toString() {
		return "ObjectId [id=" + id + ", systemId=" + systemID + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		ObjectId objectId = (ObjectId) o;
		return Objects.equals(id, objectId.id) && Objects.equals(systemID, objectId.systemID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, systemID);
	}


}

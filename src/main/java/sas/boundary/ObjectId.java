package sas.boundary;

import java.util.Objects;

public class ObjectId {
	private String id;
	private String systemId;


	public ObjectId() {
	}

	public ObjectId(String id, String systemId){
		this.id = id;
		this.systemId = systemId;
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

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		ObjectId objectId = (ObjectId) o;
		return Objects.equals(id, objectId.id) && Objects.equals(systemId, objectId.systemId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, systemId);
	}



}

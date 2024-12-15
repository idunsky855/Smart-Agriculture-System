package aii.logic;

import java.util.Date;
import java.util.Map;


public class ObjectBoundary {
	private ObjectId objectId;
	private String type;
	private String alias;
	private String status;
	private Location location;
	private Boolean active;
	private Date creationTimestamp;
	private CreatedBy createdBy;
	private Map<String, Object> objectDetails;
	
	public ObjectBoundary() {	
	}

	// for tests
	public ObjectBoundary(ObjectId objectId, String type, String alias, String status, Location location, Boolean active, Date creationTimestamp, CreatedBy createdBy, Map<String, Object> objectDetails) {
		this.objectId = objectId;
		this.type = type;
		this.alias = alias;
		this.status = status;
		this.location = location;
		this.active = active;
		this.creationTimestamp = creationTimestamp;
		this.createdBy = createdBy;
		this.objectDetails = objectDetails;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public Map<String, Object> getObjectDetails() {
		return objectDetails;
	}

	public void setObjectDetails(Map<String, Object> objectDetails) {
		this.objectDetails = objectDetails;
	}
	
	public ObjectId getObjectId(){
		return this.objectId;
	}
	
	public void setObjectId(ObjectId objId){
		this.objectId = objId;
	}
	
	public Location getLocation(){
		return this.location;
	}
	
	public void setLocation(Location loc){
		this.location = loc;
	}
	
	
	public CreatedBy getCreatedBy(){
		return this.createdBy;
	}
	
	public void setCreatedBy(CreatedBy cb){
		this.createdBy = cb;
	}


	@Override
	public String toString() {
		StringBuffer sb =  new StringBuffer();
		sb.append("ObjectBoundary ");
		sb.append("[ ");
		sb.append("objectID= " + this.objectId + ", ");
		sb.append("type= " + this.type + ", ");
		sb.append("alias= " + this.alias + ", ");
		sb.append("status= "+ this.status + ", ");
		sb.append("location= "+ this.location + ", ");
		sb.append("active="+ this.active + ", ");
		sb.append("creationTimestamp="+ this.creationTimestamp + ", ");
		sb.append("createdBy= "+ this.createdBy +  ", ");
		sb.append("objectDetails= "+this.objectDetails);
		sb.append(" ]");
		return  sb.toString();
	}
}


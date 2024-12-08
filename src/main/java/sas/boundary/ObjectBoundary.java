package sas.boundary;

import sas.boundary.CreatedBy;
import sas.boundary.Location;
import sas.boundary.ObjectId;
import sas.utils.Constants;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ObjectBoundary {
	private ObjectId objectId;
	private String type;
	private String alias;
	private String status;
	private Location location;
	private boolean active;
	private Date creationTimestamp;
	private CreatedBy createdBy;
	private Map<String, Object> objectDetails;
	
	
	public ObjectBoundary() {	
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
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


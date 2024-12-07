package demo;

import demo.Constants;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ObjectsBoundary {
	private Map<String,String> objectId;
	private Integer id; 
	private final String systemId = Constants.SYSTEM_ID ;
	private String type;
	private String alias;
	private String status;
	private Map<String,Double> location;
	private Double lat, lng;
	private boolean active;
	private Date creationTimestamp;
	private Map<String,Object> objectDetails;
	private String email;
	private Map<String,Map<String,String>> createdBy;
	
	/* Todo:
			Create User, Location classes to replace the maps 
			replace all calculations and get the info from the controller
	*/
	
	public ObjectsBoundary() {	
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Map<String,Object> getObjectDetails() {
		return objectDetails;
	}

	public void setObjectDetails(Map<String,Object> objectDetails) {
		this.objectDetails = objectDetails;
	}

	public String getSystemId() {
		return systemId;
	}
	
	public Map<String, String> getObjectId(){
		this.objectId = new HashMap<String,String>();
		this.objectId.put("id", this.id.toString());
		this.objectId.put("systemID", this.systemId);
		return this.objectId;
	}
	
	public Map<String, Double> getLocation(){
		this.location = new HashMap<String,Double>();
		this.location.put("lat", this.lat);
		this.location.put("lng", this.lng);
		return this.location;
	}
	
	public Map<String,Map<String,String>> getCreatedBy(){
		Map<String,String> userID = new HashMap<String,String>();
		userID.put("systemID", this.systemId);
		userID.put("email", this.email);
		this.createdBy = new HashMap<String,Map<String,String>>();
		this.createdBy.put("createdBy", userID);
		return this.createdBy;
	}
	

	@Override
	public String toString() {
		StringBuffer sb =  new StringBuffer();
		sb.append("ObjectBoundary ");
		sb.append("[ ");
		sb.append("objectID= " + this.getObjectId() + ", ");
		sb.append("type= " + this.type + ", ");
		sb.append("alias= " + this.alias + ", ");
		sb.append("status= "+ this.status + ", ");
		sb.append("location= "+ this.getLocation() + ", ");
		sb.append("active="+ this.active + ", ");
		sb.append("creationTimestamp="+ this.creationTimestamp + ", ");
		sb.append("createdBy= "+ this.getCreatedBy() +  ", ");
		sb.append("objectDetails= "+this.objectDetails);
		sb.append(" ]");
		return  sb.toString();
	}

}


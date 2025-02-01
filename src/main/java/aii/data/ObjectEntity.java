package aii.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import aii.logic.Location;
import aii.logic.ObjectId;
import aii.logic.converters.MapToStringConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "OBJECTS")
public class ObjectEntity {

    @Id
    private String objectId;
    private String type;
    private String alias;
    private String status;

    private Integer currentSoilMoistureLevel;
    private Integer currentLightLevelIntensity;
    
    private Integer optimalSoilMoistureLevel;
    private Integer optimalLightLevelIntensity;

    private String relatedObjectId;

    private double lat;
    private double lng;

    private boolean active;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    private String createdBy;

    @Lob
    @Convert(converter = MapToStringConverter.class)
    private Map<String, Object> objectDetails;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId.getSystemID() + "@@" + objectId.getId();
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

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLocation(Location location) {
        this.lat = location.getLat();
        this.lng = location.getLng();
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Map<String, Object> getObjectDetails() {
        if (this.objectDetails == null)
            this.objectDetails = new HashMap<String, Object>();
        return objectDetails;
    }

    public void setObjectDetails(Map<String, Object> objectDetails) {
        this.objectDetails = objectDetails;
    }

    
    public Integer getCurrentSoilMoistureLevel(){
        return this.currentSoilMoistureLevel;
    }
    public Integer getCurrentLightLevelIntensity(){
        return this.currentLightLevelIntensity;
    }
    public Integer getOptimalSoilMoistureLevel(){
        return this.optimalSoilMoistureLevel;
    }
    public Integer getOptimalLightLevelIntensity(){
        return this.optimalLightLevelIntensity;
    }
    public String getRelatedObjectId(){
        return this.relatedObjectId;
    }
    
    public void setCurrentSoilMoistureLevel(int currentSoilMoistureLevel){
        this.currentSoilMoistureLevel = currentSoilMoistureLevel;
    }

    public void setCurrentLightLevelIntensity(int currentLightLevelIntensity){
        this.currentLightLevelIntensity = currentLightLevelIntensity;
    }
    public void setOptimalSoilMoistureLevel(int optimalSoilMoistureLevel){
        this.optimalSoilMoistureLevel = optimalSoilMoistureLevel;
    }
    public void setOptimalLightLevelIntensity(int optimalLightLevelIntensity){
        this.optimalLightLevelIntensity = optimalLightLevelIntensity;
    }
    public void setRelatedObjectId(String relatedObjectId){
        this.relatedObjectId = relatedObjectId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ObjectEntity [objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", Status=" + status
                + ", location=[lng = " + lng + ", lat = " + lat + "], active=" + active + ", creationTime="
                + creationTime + ", createdBy="
                + createdBy + ", objectDetails=" + objectDetails);

        if (currentSoilMoistureLevel != null && optimalSoilMoistureLevel != null &&
             currentLightLevelIntensity != null && optimalLightLevelIntensity != null){
            
            sb.append(", soil moisture: [ optimal=" + optimalSoilMoistureLevel + 
            ", current=" + currentSoilMoistureLevel + "], light level: [ optimal=" +
             optimalLightLevelIntensity + ", current=" + currentSoilMoistureLevel +" ]");
        } 
        
        if (relatedObjectId != null){
            sb.append(", relatedObjectId="+ relatedObjectId );
        }
        
        sb.append("]");
        return sb.toString();
    }
}

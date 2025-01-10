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
    
    private String location;
    
    private boolean active;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    
    private String createdBy;
    
    @Lob
    @Convert( converter = MapToStringConverter.class)
    private Map<String, Object> objectDetails;


    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setObjectId(ObjectId objectId){
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

    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }

    public void setLocation(Location location) {
        this.location = location.getLat() + "@@" + location.getLng();
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

    public String getCreatedBy(){
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Map<String, Object> getObjectDetails() {
        if(this.objectDetails == null)
            this.objectDetails = new HashMap<String, Object>();
        return objectDetails;
    }

    public void setObjectDetails(Map<String, Object> objectDetails) {
        this.objectDetails = objectDetails;
    }

    @Override
    public String toString() {
        return "ObjectEntity [objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", Status=" + status
                + ", location=" + location + ", active=" + active + ", creationTime=" + creationTime + ", createdBy="
                + createdBy + ", objectDetails=" + objectDetails + "]";
    }
    
}

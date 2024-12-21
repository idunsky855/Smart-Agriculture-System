package aii.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import aii.logic.CreatedBy;
import aii.logic.Location;
import aii.logic.ObjectId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "OBJECTS")
public class ObjectEntity {
    
    @Id
    private String objectId;
    private String type;
    private String alias;
    private String Status;
    
    @Transient
    private Location location;
    
    @Transient
    private Boolean active;
    
    @Transient
    private Date creationTime;
    
    @Transient
    private CreatedBy createdBy;
    
    @Transient
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
        return Status;
    }

    public void setStatus(String status) {
       Status = status;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
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
        return "ObjectEntity [objectId=" + objectId + ", type=" + type + ", alias=" + alias + ", Status=" + Status
                + ", location=" + location + ", active=" + active + ", creationTime=" + creationTime + ", createdBy="
                + createdBy + ", objectDetails=" + objectDetails + "]";
    }
    
}

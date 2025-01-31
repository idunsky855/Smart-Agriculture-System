package com.example.androidmobileclient.plant;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Plant {

    private ObjectId objectId;
    private String type;
    private String alias;
    private String status;
    private Location location;
    private boolean active;
    private String creationTimestamp;
    private CreatedBy createdBy;
    private HashMap<String, Object> objectDetails; // = new HashMap<>();
    /*private Integer currentSoilMoistureLevel;
    private Integer currentLightLevelIntensity;
    private Integer optimalSoilMoistureLevel;
    private Integer optimalLightLevelIntensity;*/
    //private String image;


    public Plant() {
        objectDetails = new HashMap<>();
    }

    public String getType() {
        return type;
    }

    public Plant setType(String type) {
        this.type = type;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public Plant setAlias(String alias) {
        this.alias = alias;
        return this;
    }

   /* public String getImage() {
        return image;
    }

    public Plant setImage(String image) {
        this.image = image;
        return this;
    }*/

    public HashMap<String, Object> getObjectDetails() {
        return objectDetails;
    }

    public Plant setObjectDetails(HashMap<String, Object> objectDetails) {
        this.objectDetails = objectDetails;
        return this;
    }

    public class UserId{
        public String systemID;
        public String email;

        public UserId(String systemID, String email) {
            this.systemID = systemID;
            this.email = email;
        }
    }

    public class CreatedBy{
        public UserId userId;

        public CreatedBy(UserId userId) {
            this.userId = userId;
        }
    }

    public class Location{
        public double lat;
        public double lng;

        public Location() {
        }

        public Location(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }

    public class ObjectId{
        public String systemID;
        public String id;
    }

    public boolean isActive() {
        return active;
    }

    public Plant setActive(boolean active) {
        this.active = active;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Plant setStatus(String status) {
        this.status = status;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public Plant setLocation(Location location) {
        this.location = location;
        return this;
    }

    public Plant setLocation(double lat, double lng) {
        this.location = new Location(lat, lng);
        return this;
    }


    public Plant setCreatedBy(String systemID, String userEmail) {
        this.createdBy = new CreatedBy(new UserId(systemID,userEmail));
        return this;
    }


    public Integer getCurrentSoilMoistureLevel() {
        //return currentSoilMoistureLevel;
        if(objectDetails.containsKey("currentSoilMoistureLevel") &&
                objectDetails.get("currentSoilMoistureLevel") != null) {
            double value = (Double) objectDetails.get("currentSoilMoistureLevel");
            return (int) value;
        }
        return null;
    }


    public Integer getCurrentLightLevelIntensity() {
        if(objectDetails.containsKey("currentLightLevelIntensity") &&
                objectDetails.get("currentLightLevelIntensity") != null) {
            double value = (Double) objectDetails.get("currentLightLevelIntensity");
            return (int) value;
        }
        return null;
    }


    public Integer getOptimalSoilMoistureLevel() {
        if(objectDetails.containsKey("optimalSoilMoistureLevel") &&
                objectDetails.get("optimalSoilMoistureLevel") != null) {
            double value = (Double)objectDetails.get("optimalSoilMoistureLevel");
            return (int) value;
        }
        return null;
    }

    public Plant setOptimalSoilMoistureLevel(Integer optimalSoilMoistureLevel) {
        objectDetails.put("optimalSoilMoistureLevel",optimalSoilMoistureLevel);
        return this;
    }

    public Integer getOptimalLightLevelIntensity() {
        if(objectDetails.containsKey("optimalLightLevelIntensity") &&
                objectDetails.get("optimalLightLevelIntensity") != null) {
            double value = (Double)objectDetails.get("optimalLightLevelIntensity");
            return (int) value;

        }
        return null;
    }

    public Plant setOptimalLightLevelIntensity(Integer optimalLightLevelIntensity) {
        objectDetails.put("optimalLightLevelIntensity",optimalLightLevelIntensity);
        return this;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public Plant setObjectId(ObjectId objectId) {
        this.objectId = objectId;
        return this;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    public Plant setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public Plant setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "objectId=" + objectId +
                ", type='" + type + '\'' +
                ", alias='" + alias + '\'' +
                ", status='" + status + '\'' +
                ", location=" + location +
                ", active=" + active +
                ", creationTimestamp='" + creationTimestamp + '\'' +
                ", createdBy=" + createdBy +
                ", objectDetails=" + objectDetails +
                '}';
    }
}

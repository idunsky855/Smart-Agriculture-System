package com.example.androidmobileclient.command;

import java.util.HashMap;

public class IrrigationControllerObject {
    private ObjectId objectId;
    private String type;
    private String alias;
    private String status;
    private Location location;
    private boolean active;
    private String creationTimestamp;
    private CreatedBy createdBy;
    private HashMap<String, Object> objectDetails;

    public IrrigationControllerObject() {
        objectDetails = new HashMap<>();
    }

    public String getType() {
        return type;
    }

    public IrrigationControllerObject setType(String type) {
        this.type = type;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public IrrigationControllerObject setAlias(String alias) {
        this.alias = alias;
        return this;
    }


    public HashMap<String, Object> getObjectDetails() {
        return objectDetails;
    }

    public IrrigationControllerObject setObjectDetails(HashMap<String, Object> objectDetails) {
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

    public IrrigationControllerObject setActive(boolean active) {
        this.active = active;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public IrrigationControllerObject setStatus(String status) {
        this.status = status;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public IrrigationControllerObject setLocation(Location location) {
        this.location = location;
        return this;
    }

    public IrrigationControllerObject setLocation(double lat, double lng) {
        this.location = new Location(lat, lng);
        return this;
    }

    public ObjectId getObjectId() {
        return objectId;
    }



}

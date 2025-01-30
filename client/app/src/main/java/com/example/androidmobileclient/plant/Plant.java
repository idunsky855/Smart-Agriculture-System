package com.example.androidmobileclient.plant;

import java.util.HashMap;
import java.util.Map;

public class Plant {

    private String type;
    public String alias;
    public String status;
    public boolean active;
    private HashMap<String, String> objectDetails = new HashMap<>();
    public Location location;
    public String creationTimestamp;
    public CreatedBy createdBy;
    public ObjectId objectId;

    private String image;


    public Plant() {}

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

    public String getImage() {
        return image;
    }

    public Plant setImage(String image) {
        this.image = image;
        return this;
    }

    public HashMap<String, String> getObjectDetails() {
        return objectDetails;
    }

    public Plant setObjectDetails(HashMap<String, String> objectDetails) {
        this.objectDetails = objectDetails;
        return this;
    }



    public class UserId{
        public String systemID;
        public String email;
    }

    public class CreatedBy{
        public UserId userId;
    }

    public class Location{
        public double lat;
        public double lng;
    }

    public class ObjectId{
        public String systemID;
        public String id;
    }
}

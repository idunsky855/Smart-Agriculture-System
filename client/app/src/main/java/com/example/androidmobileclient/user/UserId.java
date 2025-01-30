package com.example.androidmobileclient.user;

public class UserId {

    private String systemID;
    private String email;

    public UserId() {}

    public String getSystemID() {
        return systemID;
    }

    public UserId setSystemID(String systemID) {
        this.systemID = systemID;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserId setEmail(String email) {
        this.email = email;
        return this;
    }
}

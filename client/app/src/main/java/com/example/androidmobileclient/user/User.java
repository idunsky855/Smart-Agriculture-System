package com.example.androidmobileclient.user;

public class User {
    
    private UserId userId;
    private USER_ROLE role;
    private String username;
    private String avatar;

    public User() {}

    public UserId getUserId() {
        return userId;
    }

    public User setUserId(UserId userId) {
        this.userId = userId;
        return this;
    }

    public USER_ROLE getRole() {
        return role;
    }

    public User setRole(USER_ROLE role) {
        this.role = role;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }
}

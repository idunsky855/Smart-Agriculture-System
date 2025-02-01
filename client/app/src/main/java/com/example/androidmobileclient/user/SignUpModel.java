package com.example.androidmobileclient.user;

public class SignUpModel {

    private String email;
    private USER_ROLE role;
    private String username;
    private String avatar;

    public SignUpModel() {}

    public String getEmail() {
        return email;
    }

    public SignUpModel setEmail(String email) {
        this.email = email;
        return this;
    }

    public USER_ROLE getRole() {
        return role;
    }

    public SignUpModel setRole(USER_ROLE role) {
        this.role = role;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public SignUpModel setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public SignUpModel setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }
}

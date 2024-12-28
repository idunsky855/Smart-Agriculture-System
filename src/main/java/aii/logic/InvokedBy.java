package aii.logic;

public class InvokedBy {
    private UserId userId;

    // Getters and setters
    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    // toString method
    @Override
    public String toString() {
        return "InvokedBy [userId=" + userId + "]";
    }
}

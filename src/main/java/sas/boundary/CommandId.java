package sas.boundary;

public class CommandId {
    private String systemID;
    private String id;

    // Getters and setters
    public String getSystemID() {
        return systemID;
    }

    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // toString method
    @Override
    public String toString() {
        return "CommandId: [" +
                "systemID='" + systemID + '\'' +
                ", id='" + id + '\'' +
                ']';
    }
}

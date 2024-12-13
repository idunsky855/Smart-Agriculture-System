package sas.boundary;

public class TargetObject {
    private ObjectId objectId;

    // Getters and setters
    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    // toString method
    @Override
    public String toString() {
        return "TargetObject [objectId=" + objectId + "]";
    }
}

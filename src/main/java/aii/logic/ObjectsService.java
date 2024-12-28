package aii.logic;

import java.util.List;
import java.util.Optional;

public interface ObjectsService {
    public ObjectBoundary create(String userSystemID, String userEmail, ObjectBoundary object);
    public ObjectBoundary update(String userSystemID, String userEmail, String objectSystemID, String objectId, ObjectBoundary update);
    public List<ObjectBoundary> getAll(String userSystemID, String userEmail);
    public Optional<ObjectBoundary> getSpecificObject(String userSystemID, String userEmail, String objectSystemID, String objectId);
    public void deleteAllObjects(String adminSystemID, String adminEmail);
}

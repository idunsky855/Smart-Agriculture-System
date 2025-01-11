package aii.logic;

import java.util.List;

public interface EnhancedObjectsService extends ObjectsService {

    public List<ObjectBoundary> getAll(String userSystemID, String userEmail, int page, int size);

    public List<ObjectBoundary> getObjectsByLocation(double lat, double lng, double distance, String distanceUnits,
            String userSystemID, String userEmail, int page, int size);

    public List<ObjectBoundary> getObjectsByType(String type, String userSystemID, String userEmail, int size,
            int page);

    public List<ObjectBoundary> getObjectsByTypeAndStatus(String type, String status, String userSystemID,
            String userEmail, int size, int page);

    public List<ObjectBoundary> getObjectsByAlias(String alias, String userSystemID, String userEmail, int size, int page);

    public List<ObjectBoundary> getObjectsByLAliasPattern(String pattern, String userSystemID, String userEmail,
            int size, int page);



}

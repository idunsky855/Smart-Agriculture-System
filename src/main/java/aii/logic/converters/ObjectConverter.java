package aii.logic.converters;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import aii.data.ObjectEntity;
import aii.logic.CreatedBy;
import aii.logic.Location;
import aii.logic.ObjectBoundary;
import aii.logic.ObjectId;

@Component
public class ObjectConverter {
    public ObjectEntity toEntity(ObjectBoundary boundary) {
        // all default values and validation - in objectService
        ObjectEntity entity = new ObjectEntity();
        entity.setObjectId(boundary.getObjectId());
        entity.setType(boundary.getType());
        entity.setAlias(boundary.getAlias());
        entity.setStatus(boundary.getStatus());
        entity.setLocation(boundary.getLocation());
        entity.setActive(boundary.getActive());
        entity.setCreationTime(boundary.getCreationTimestamp());
        entity.setCreatedBy(boundary.getCreatedBy().getUserId().getSystemID() + "@@"
                + boundary.getCreatedBy().getUserId().getEmail());

        Map<String, Object> details = boundary.getObjectDetails();
        Set<String> keys = details.keySet();


        if (keys.contains("currentSoilMoistureLevel") &&
                details.get("currentSoilMoistureLevel") != null &&
                details.get("currentSoilMoistureLevel") instanceof Integer) {

            int value = (int) details.get("currentSoilMoistureLevel");
            if( value <= 100 && value >=0 )
                entity.setCurrentSoilMoistureLevel(value);
        }
        details.remove("currentSoilMoistureLevel"); // for optimal storage in db

        if (keys.contains("optimalSoilMoistureLevel") &&
                details.get("optimalSoilMoistureLevel") != null &&
                details.get("optimalSoilMoistureLevel") instanceof Integer) {

            int value = (int) details.get("optimalSoilMoistureLevel");
            if( value <= 100 && value >=0 )
                entity.setOptimalSoilMoistureLevel(value);
        }
        details.remove("optimalSoilMoistureLevel"); // for optimal storage in db

        if (keys.contains("currentLightLevelIntensity") &&
                details.get("currentLightLevelIntensity") != null &&
                details.get("currentLightLevelIntensity") instanceof Integer) {

            int value = (int) details.get("currentLightLevelIntensity");
            if( value <= 100 && value >=0 )
                entity.setCurrentLightLevelIntensity(value);
        }
        details.remove("currentLightLevelIntensity"); // for optimal storage in db

        if (keys.contains("optimalLightLevelIntensity") &&
                details.get("optimalLightLevelIntensity") != null &&
                details.get("optimalLightLevelIntensity") instanceof Integer) {

            int value = (int) details.get("optimalLightLevelIntensity");
            if( value <= 100 && value >=0 )
                entity.setOptimalLightLevelIntensity(value);
        }
        details.remove("optimalLightLevelIntensity"); // for optimal storage in db

        if (keys.contains("relatedObjectId") &&
                details.get("relatedObjectId") != null) {

            String relatedObjectId = details.get("relatedObjectId").toString();
            if(!relatedObjectId.isBlank())
                entity.setRelatedObjectId(relatedObjectId);
        }
        details.remove("relatedObjectId"); // for optimal storage in db

        entity.setObjectDetails(details);
        return entity;
    }

    public ObjectBoundary toBoundary(ObjectEntity entity) {
        ObjectBoundary boundary = new ObjectBoundary();
        String[] objIdStr = entity.getObjectId().split("@@");
        boundary.setObjectId(new ObjectId(objIdStr[1], objIdStr[0]));
        boundary.setType(entity.getType());
        boundary.setAlias(entity.getAlias());
        boundary.setStatus(entity.getStatus());
        boundary.setLocation(new Location(entity.getLat(), entity.getLng()));
        boundary.setActive(entity.getActive());
        boundary.setCreationTimestamp(entity.getCreationTime());

        String[] createdByString = entity.getCreatedBy().split("@@");
        String systemID = createdByString[0];
        String userEmail = createdByString[1];

        boundary.setCreatedBy(new CreatedBy(systemID, userEmail));

        Map<String, Object> details = entity.getObjectDetails();

        if (entity.getCurrentSoilMoistureLevel() != null) {
            details.put("currentSoilMoistureLevel", entity.getCurrentSoilMoistureLevel());
        }

        if (entity.getOptimalSoilMoistureLevel() != null) {
            details.put("optimalSoilMoistureLevel", entity.getOptimalSoilMoistureLevel());
        }

        if (entity.getCurrentLightLevelIntensity() != null) {
            details.put("currentLightLevelIntensity", entity.getCurrentLightLevelIntensity());
        }

        if (entity.getOptimalLightLevelIntensity() != null) {
            details.put("optimalLightLevelIntensity", entity.getOptimalLightLevelIntensity());
        }

        if (entity.getRelatedObjectId() != null) {
            details.put("relatedObjectId", entity.getRelatedObjectId());
        }

        boundary.setObjectDetails(details);
        return boundary;
    }
}

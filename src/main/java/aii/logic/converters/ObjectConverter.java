package aii.logic.converters;

import org.springframework.stereotype.Component;
import aii.logic.ObjectBoundary;
import aii.logic.ObjectId;
import aii.logic.CreatedBy;
import aii.data.ObjectEntity;


@Component
public class ObjectConverter {
    public ObjectEntity toEntity(ObjectBoundary boundary){
        // all default values and validation - in objectService
        ObjectEntity entity = new ObjectEntity();
		entity.setObjectId(boundary.getObjectId());
        entity.setType(boundary.getType());
        entity.setAlias(boundary.getAlias());
        entity.setStatus(boundary.getStatus());
		entity.setLocation(boundary.getLocation());
		entity.setActive(boundary.getActive());
        entity.setCreationTime(boundary.getCreationTimestamp());
		entity.setCreatedBy(boundary.getCreatedBy().getUserId().getSystemID() + "@@" + boundary.getCreatedBy().getUserId().getEmail());
		entity.setObjectDetails(boundary.getObjectDetails());
		return entity;
    }


    public ObjectBoundary toBoundary(ObjectEntity entity){
        ObjectBoundary boundary = new ObjectBoundary();
        String[] objIdStr = entity.getObjectId().split("@@");
		boundary.setObjectId(new ObjectId(objIdStr[1], objIdStr[0]));
		boundary.setType(entity.getType());
		boundary.setAlias(entity.getAlias());
		boundary.setStatus(entity.getStatus());
		boundary.setLocation(entity.getLocation());
		boundary.setActive(entity.getActive());
		boundary.setCreationTimestamp(entity.getCreationTime());
        
        String[] createdByString = entity.getCreatdBy().split("@@");
        String systemID = createdByString[0];
        String userEmail = createdByString[1];
		
        boundary.setCreatedBy(new CreatedBy(systemID, userEmail));
		boundary.setObjectDetails(entity.getObjectDetails());
        return boundary;
    }
}

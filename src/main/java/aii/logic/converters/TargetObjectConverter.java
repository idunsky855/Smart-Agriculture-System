package aii.logic.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import aii.logic.TargetObject;
import aii.logic.ObjectId;

@Converter
public class TargetObjectConverter implements AttributeConverter<TargetObject, String> {

    @Override
    public String convertToDatabaseColumn(TargetObject attribute) {
        if (attribute == null || attribute.getObjectId() == null) {
            return null;
        }

        String systemID = attribute.getObjectId().getSystemID();
        String id = attribute.getObjectId().getId();

        // Return a single string representation
        return systemID + "@@" + id;
    }

    @Override
    public TargetObject convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        // Split the string back into systemID and id
        String[] parts = dbData.split("@@");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid data for TargetObject: " + dbData);
        }

        // Reconstruct the ObjectId and TargetObject
        ObjectId objectId = new ObjectId();
        objectId.setSystemID(parts[0]);
        objectId.setId(parts[1]);

        TargetObject targetObject = new TargetObject();
        targetObject.setObjectId(objectId);

        return targetObject;
    }
}

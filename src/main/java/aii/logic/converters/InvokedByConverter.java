package aii.logic.converters;

import aii.logic.InvokedBy;
import aii.logic.UserId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class InvokedByConverter implements AttributeConverter<InvokedBy, String> {

    @Override
    public String convertToDatabaseColumn(InvokedBy invokedBy) {
        // Handle null checks
        if (invokedBy == null || invokedBy.getUserId() == null) {
            return null;
        }

        // Extract fields from UserId
        UserId userId = invokedBy.getUserId();
        String systemID = userId.getSystemID();
        String email = userId.getEmail();

        return systemID + "@@" + email;
    }

    @Override
    public InvokedBy convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        // Split back into systemID and email
        String[] parts = dbData.split("@@");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid data for InvokedBy: " + dbData);
        }

        // Reconstruct the UserId
        UserId userId = new UserId();
        userId.setSystemID(parts[0]);
        userId.setEmail(parts[1]);

        // Wrap in InvokedBy
        InvokedBy invokedBy = new InvokedBy();
        invokedBy.setUserId(userId);


        return invokedBy;
    }
}

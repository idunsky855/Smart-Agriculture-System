package aii.logic.converters;

import com.fasterxml.jackson.databind.ObjectMapper;

import aii.logic.Location;
import jakarta.persistence.AttributeConverter;

public class LocationConverter implements AttributeConverter<Location,String>{
    private ObjectMapper jackson;

    public LocationConverter(){
        this.jackson = new ObjectMapper();
    }

    @Override
    public String convertToDatabaseColumn(Location attribute) {
        try{
            return this.jackson.writeValueAsString(attribute);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Location convertToEntityAttribute(String dbData) {
        try{
            return this.jackson.readValue(dbData, Location.class);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}

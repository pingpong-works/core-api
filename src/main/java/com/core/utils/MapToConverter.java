package com.core.utils;

import com.core.exception.DataConversionException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.Map;

@Converter
public class MapToConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new DataConversionException("Could not convert map to JSON string.", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
            return objectMapper.readValue(dbData, typeRef);

        } catch (IOException e) {
            throw new DataConversionException("Could not convert JSON string to map.", e);
        }
    }
}

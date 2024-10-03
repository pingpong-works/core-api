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
            // 먼저 문자열에 이중 인코딩이 되어 있는지 확인
            if (dbData.startsWith("\"") && dbData.endsWith("\"")) {
                dbData = dbData.substring(1, dbData.length() - 1);  // 양 끝의 따옴표 제거
            }
            // 필요시 이스케이프 문자 처리
            dbData = dbData.replace("\\\"", "\"");

            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
            return objectMapper.readValue(dbData, typeRef);

        } catch (IOException e) {
            throw new DataConversionException("Could not convert JSON string to map.", e);
        }
    }
}

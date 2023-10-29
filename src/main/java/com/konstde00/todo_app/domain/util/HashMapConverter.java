package com.konstde00.todo_app.domain.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class HashMapConverter implements AttributeConverter<HashMap<String, String>, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(HashMap<String, String> attribute) {
    if (attribute == null || attribute.isEmpty()) {
      return null;
    }

    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (IOException e) {
      // Handle the exception as needed
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public HashMap<String, String> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return new HashMap<>();
    }

    try {
      return objectMapper.readValue(dbData, new TypeReference<HashMap<String, String>>() {});
    } catch (IOException e) {
      // Handle the exception as needed
      e.printStackTrace();
      return new HashMap<>();
    }
  }
}

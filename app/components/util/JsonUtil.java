package components.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class JsonUtil {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final TypeReference<List<String>> STRING_LIST_TYPE_REFERENCE = new TypeReference<List<String>>() {
  };

  public static String convertListToJson(List<String> list) {
    try {
      return OBJECT_MAPPER.writeValueAsString(list);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to parse list as JSON.", e);
    }
  }

  public static List<String> convertJsonToList(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, STRING_LIST_TYPE_REFERENCE);
    } catch (IOException e) {
      throw new RuntimeException("Failed to parse JSON as list.", e);
    }
  }

}

package components.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.File;

import java.io.IOException;
import java.util.List;

public class JsonUtil {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final TypeReference<List<String>> STRING_LIST_TYPE_REFERENCE = new TypeReference<List<String>>() {
  };

  private static final TypeReference<List<File>> FILE_LIST_TYPE_REFERENCE = new TypeReference<List<File>>() {
  };

  public static String convertListToJson(List<String> list) {
    try {
      return OBJECT_MAPPER.writeValueAsString(list);
    } catch (JsonProcessingException jpe) {
      throw new RuntimeException("Failed to convert list to json.", jpe);
    }
  }

  public static List<String> convertJsonToList(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, STRING_LIST_TYPE_REFERENCE);
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to convert json to list.", ioe);
    }
  }

  public static String convertFilesToJson(List<File> files) {
    try {
      return OBJECT_MAPPER.writeValueAsString(files);
    } catch (JsonProcessingException jpe) {
      throw new RuntimeException("Failed to convert files to json.", jpe);
    }
  }

  public static List<File> convertJsonToFiles(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, FILE_LIST_TYPE_REFERENCE);
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to convert json to files.", ioe);
    }
  }

}

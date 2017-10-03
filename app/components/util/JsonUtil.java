package components.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Document;
import org.apache.commons.collections4.ListUtils;
import uk.gov.bis.lite.exporterdashboard.api.File;

import java.io.IOException;
import java.util.List;

public class JsonUtil {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final TypeReference<List<String>> STRING_LIST_TYPE_REFERENCE = new TypeReference<List<String>>() {
  };

  private static final TypeReference<List<File>> FILE_LIST_TYPE_REFERENCE = new TypeReference<List<File>>() {
  };

  private static final TypeReference<List<Document>> DOCUMENT_LIST_TYPE_REFERENCE = new TypeReference<List<Document>>() {
  };

  public static String convertFileToJson(File file) {
    return toJson(file);
  }

  public static File convertJsonToFile(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, File.class);
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to convert json to file.", ioe);
    }
  }

  public static <E> String convertListToJson(List<? extends E> list) {
    return toJson(ListUtils.emptyIfNull(list));
  }

  public static List<String> convertJsonToList(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, STRING_LIST_TYPE_REFERENCE);
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to convert json to list.", ioe);
    }
  }

  public static List<File> convertJsonToFiles(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, FILE_LIST_TYPE_REFERENCE);
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to convert json to files.", ioe);
    }
  }

  public static List<Document> convertJsonToDocuments(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, DOCUMENT_LIST_TYPE_REFERENCE);
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to convert json to documents.", ioe);
    }
  }

  private static String toJson(Object object) {
    try {
      return OBJECT_MAPPER.writeValueAsString(object);
    } catch (JsonProcessingException jpe) {
      throw new RuntimeException("Failed to convert object to json", jpe);
    }
  }

}

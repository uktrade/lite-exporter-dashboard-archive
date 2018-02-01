package components.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Attachment;
import models.Document;
import models.OutcomeDocument;
import org.apache.commons.collections4.ListUtils;

import java.io.IOException;
import java.util.List;

public class JsonUtil {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final TypeReference<List<String>> STRING_LIST_TYPE_REFERENCE = new TypeReference<List<String>>() {};
  private static final TypeReference<List<Attachment>> ATTACHMENT_LIST_TYPE_REFERENCE = new TypeReference<List<Attachment>>() {};
  private static final TypeReference<List<OutcomeDocument>> OUTCOME_DOCUMENT_LIST_TYPE_REFERENCE = new TypeReference<List<OutcomeDocument>>() {};

  public static String convertDocumentToJson(Document document) {
    return toJson(document);
  }

  public static Document convertJsonToDocument(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, Document.class);
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

  public static List<Attachment> convertJsonToAttachments(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, ATTACHMENT_LIST_TYPE_REFERENCE);
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to convert json to attachments.", ioe);
    }
  }

  public static List<OutcomeDocument> convertJsonToOutcomeDocuments(String json) {
    try {
      return OBJECT_MAPPER.readValue(json, OUTCOME_DOCUMENT_LIST_TYPE_REFERENCE);
    } catch (IOException ioe) {
      throw new RuntimeException("Failed to convert json to outcome documents.", ioe);
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

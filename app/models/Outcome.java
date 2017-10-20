package models;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

public class Outcome {

  @NotBlank
  private String id;

  @NotBlank
  private String appId;

  @NotBlank
  private String createdByUserId;

  @NotNull
  private List<String> recipientUserIds;

  @NotNull
  private Long createdTimestamp;

  @NotEmpty
  private List<Document> documents;

  public Outcome(String id, String appId, String createdByUserId, List<String> recipientUserIds, Long createdTimestamp, List<Document> documents) {
    this.id = id;
    this.appId = appId;
    this.createdByUserId = createdByUserId;
    this.recipientUserIds = recipientUserIds;
    this.createdTimestamp = createdTimestamp;
    this.documents = documents;
  }

  public String getId() {
    return id;
  }

  public String getAppId() {
    return appId;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public List<String> getRecipientUserIds() {
    return recipientUserIds;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public List<Document> getDocuments() {
    return documents;
  }

}

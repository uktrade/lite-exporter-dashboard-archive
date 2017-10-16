package models;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

public class Rfi {

  @NotBlank
  private final String id;
  @NotBlank
  private final String appId;
  @NotNull
  private final Long createdTimestamp;
  @NotNull
  private final Long dueTimestamp;
  @NotBlank
  private final String createdByUserId;
  @NotEmpty
  private final List<String> recipientUserIds;
  @NotBlank
  private final String message;

  public Rfi(String id, String appId, Long createdTimestamp, Long dueTimestamp, String createdByUserId, List<String> recipientUserIds, String message) {
    this.id = id;
    this.appId = appId;
    this.createdTimestamp = createdTimestamp;
    this.dueTimestamp = dueTimestamp;
    this.createdByUserId = createdByUserId;
    this.recipientUserIds = recipientUserIds;
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public String getAppId() {
    return appId;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public Long getDueTimestamp() {
    return dueTimestamp;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public List<String> getRecipientUserIds() {
    return recipientUserIds;
  }

  public String getMessage() {
    return message;
  }

}

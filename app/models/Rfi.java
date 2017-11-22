package models;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

public class Rfi {

  @NotBlank
  private final String id;
  @NotBlank
  private final String caseReference;
  @NotNull
  private final Long createdTimestamp;
  @NotNull
  private final Long dueTimestamp;
  @NotBlank
  private final String createdByUserId;
  @NotNull
  private final List<String> recipientUserIds;
  @NotBlank
  private final String message;

  public Rfi(String id, String caseReference, Long createdTimestamp, Long dueTimestamp, String createdByUserId, List<String> recipientUserIds, String message) {
    this.id = id;
    this.caseReference = caseReference;
    this.createdTimestamp = createdTimestamp;
    this.dueTimestamp = dueTimestamp;
    this.createdByUserId = createdByUserId;
    this.recipientUserIds = recipientUserIds;
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public String getCaseReference() {
    return caseReference;
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

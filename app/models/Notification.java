package models;


import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

import javax.validation.constraints.NotNull;

public class Notification implements RecipientMessage {

  @NotBlank
  private final String id;
  @NotBlank
  private final String caseReference;
  @NotNull
  private final NotificationType notificationType;
  @NotBlank
  private final String createdByUserId;
  @NotNull
  private final Long createdTimestamp;
  @NotNull
  private final List<String> recipientUserIds;
  @NotEmpty
  private final String message;
  @NotNull
  private final Document document;

  public Notification(String id, String caseReference, NotificationType notificationType, String createdByUserId, Long createdTimestamp, List<String> recipientUserIds, String message, Document document) {
    this.id = id;
    this.caseReference = caseReference;
    this.notificationType = notificationType;
    this.createdByUserId = createdByUserId;
    this.createdTimestamp = createdTimestamp;
    this.recipientUserIds = recipientUserIds;
    this.message = message;
    this.document = document;
  }

  public String getId() {
    return id;
  }

  public String getCaseReference() {
    return caseReference;
  }

  public NotificationType getNotificationType() {
    return notificationType;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public List<String> getRecipientUserIds() {
    return recipientUserIds;
  }

  public String getMessage() {
    return message;
  }

  public Document getDocument() {
    return document;
  }

}

package models;


import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

public class Notification {

  @NotBlank
  private final String id;
  @NotBlank
  private final String appId;
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
  private final File document;

  public Notification(String id, String appId, NotificationType notificationType, String createdByUserId, Long createdTimestamp, List<String> recipientUserIds, String message, File document) {
    this.id = id;
    this.appId = appId;
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

  public String getAppId() {
    return appId;
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

  public File getDocument() {
    return document;
  }

}

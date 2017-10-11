package models;


import java.util.List;

public class Notification {

  private final String id;
  private final String appId;
  private final NotificationType notificationType;
  private final String createdByUserId;
  private final Long createdTimestamp;
  private final List<String> recipientUserIds;
  private final String message;
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

package models;


import uk.gov.bis.lite.exporterdashboard.api.File;

import java.util.List;

public class Notification {

  private String id;
  private String appId;
  private NotificationType notificationType;
  private String createdByUserId;
  private Long createdTimestamp;
  private List<String> recipientUserIds;
  private String message;
  private List<File> attachments;

  public Notification(String id, String appId, NotificationType notificationType, String createdByUserId, Long createdTimestamp, List<String> recipientUserIds, String message, List<File> attachments) {
    this.id = id;
    this.appId = appId;
    this.notificationType = notificationType;
    this.createdByUserId = createdByUserId;
    this.createdTimestamp = createdTimestamp;
    this.recipientUserIds = recipientUserIds;
    this.message = message;
    this.attachments = attachments;
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

  public List<File> getFiles() {
    return attachments;
  }

}

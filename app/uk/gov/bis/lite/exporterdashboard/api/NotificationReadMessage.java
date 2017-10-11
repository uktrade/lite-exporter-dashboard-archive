package uk.gov.bis.lite.exporterdashboard.api;

import org.hibernate.validator.constraints.NotBlank;

public class NotificationReadMessage implements ExporterDashboardMessage {

  @NotBlank
  private String notificationId;

  @NotBlank
  private String appId;

  @NotBlank
  private String createdByUserId;

  public String getNotificationId() {
    return notificationId;
  }

  public void setNotificationId(String notificationId) {
    this.notificationId = notificationId;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public void setCreatedByUserId(String createdByUserId) {
    this.createdByUserId = createdByUserId;
  }

}

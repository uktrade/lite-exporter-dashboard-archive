package models.view;

import java.util.List;

public class StatusItemView {

  private final String status;
  private final String statusExplanation;
  private final String processLabel;
  private final String processDescription;
  private final List<NotificationView> notificationViews;

  public StatusItemView(String status, String statusExplanation, String processLabel, String processDescription, List<NotificationView> notificationViews) {
    this.status = status;
    this.statusExplanation = statusExplanation;
    this.processLabel = processLabel;
    this.processDescription = processDescription;
    this.notificationViews = notificationViews;
  }

  public String getStatus() {
    return status;
  }

  public String getStatusExplanation() {
    return statusExplanation;
  }

  public String getProcessLabel() {
    return processLabel;
  }

  public String getProcessDescription() {
    return processDescription;
  }

  public List<NotificationView> getNotificationViews() {
    return notificationViews;
  }

}

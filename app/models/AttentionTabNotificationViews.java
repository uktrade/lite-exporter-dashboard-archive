package models;

import java.util.List;
import models.view.NotificationView;

public class AttentionTabNotificationViews {

  private final List<NotificationView> rfiNotificationViews;
  private final List<NotificationView> withdrawalRejectionNotificationViews;
  private final List<NotificationView> informNotificationViews;
  private final List<NotificationView> outcomeNotificationViews;
  private final List<NotificationView> stopNotificationViews;
  private final NotificationView withdrawalApprovalNotificationView;
  private final NotificationView delayNotificationView;

  public AttentionTabNotificationViews(List<NotificationView> rfiNotificationViews,
                                       List<NotificationView> withdrawalRejectionNotificationViews,
                                       List<NotificationView> informNotificationViews,
                                       List<NotificationView> outcomeNotificationViews,
                                       List<NotificationView> stopNotificationViews,
                                       NotificationView withdrawalApprovalNotificationView,
                                       NotificationView delayNotificationView) {
    this.rfiNotificationViews = rfiNotificationViews;
    this.withdrawalRejectionNotificationViews = withdrawalRejectionNotificationViews;
    this.informNotificationViews = informNotificationViews;
    this.outcomeNotificationViews = outcomeNotificationViews;
    this.stopNotificationViews = stopNotificationViews;
    this.withdrawalApprovalNotificationView = withdrawalApprovalNotificationView;
    this.delayNotificationView = delayNotificationView;
  }

  public List<NotificationView> getRfiNotificationViews() {
    return rfiNotificationViews;
  }

  public List<NotificationView> getWithdrawalRejectionNotificationViews() {
    return withdrawalRejectionNotificationViews;
  }

  public List<NotificationView> getInformNotificationViews() {
    return informNotificationViews;
  }

  public List<NotificationView> getOutcomeNotificationViews() {
    return outcomeNotificationViews;
  }

  public List<NotificationView> getStopNotificationViews() {
    return stopNotificationViews;
  }

  public NotificationView getWithdrawalApprovalNotificationView() {
    return withdrawalApprovalNotificationView;
  }

  public NotificationView getDelayNotificationView() {
    return delayNotificationView;
  }

}

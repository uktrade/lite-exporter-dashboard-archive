package models;

import java.util.List;
import models.view.NotificationView;

public class AttentionTabNotificationViews {

  private final List<NotificationView> rfiNotificationViews;
  private final List<NotificationView> withdrawalRejectionNotificationViews;
  private final List<NotificationView> informNotificationViews;
  private final List<NotificationView> outcomeNotificationViews;
  private final NotificationView stopNotificationView;
  private final NotificationView withdrawalApprovalNotificationView;
  private final NotificationView delayNotificationView;

  public AttentionTabNotificationViews(List<NotificationView> rfiNotificationViews,
                                       List<NotificationView> withdrawalRejectionNotificationViews,
                                       List<NotificationView> informNotificationViews,
                                       List<NotificationView> outcomeNotificationViews,
                                       NotificationView stopNotificationView,
                                       NotificationView withdrawalApprovalNotificationView,
                                       NotificationView delayNotificationView) {
    this.rfiNotificationViews = rfiNotificationViews;
    this.withdrawalRejectionNotificationViews = withdrawalRejectionNotificationViews;
    this.informNotificationViews = informNotificationViews;
    this.outcomeNotificationViews = outcomeNotificationViews;
    this.stopNotificationView = stopNotificationView;
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

  public NotificationView getStopNotificationView() {
    return stopNotificationView;
  }

  public NotificationView getWithdrawalApprovalNotificationView() {
    return withdrawalApprovalNotificationView;
  }

  public NotificationView getDelayNotificationView() {
    return delayNotificationView;
  }

}

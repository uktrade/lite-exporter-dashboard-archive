package models;

import java.util.List;

public class AppData {

  private final Application application;
  private final List<StatusUpdate> statusUpdates;
  private final List<WithdrawalRequest> withdrawalRequests;
  private final List<WithdrawalRejection> withdrawalRejections;
  private final WithdrawalApproval withdrawalApproval;
  private final List<Rfi> rfiList;
  private final List<RfiReply> rfiReplies;
  private final List<RfiWithdrawal> rfiWithdrawals;
  private final Notification delayNotification;
  private final Notification stopNotification;
  private final List<Notification> informNotifications;
  private final List<Outcome> outcomes;

  public AppData(Application application, List<StatusUpdate> statusUpdates, List<WithdrawalRequest> withdrawalRequests, List<WithdrawalRejection> withdrawalRejections, WithdrawalApproval withdrawalApproval, List<Rfi> rfiList, List<RfiReply> rfiReplies, List<RfiWithdrawal> rfiWithdrawals, Notification delayNotification, Notification stopNotification, List<Notification> informNotifications, List<Outcome> outcomes) {
    this.application = application;
    this.statusUpdates = statusUpdates;
    this.withdrawalRequests = withdrawalRequests;
    this.withdrawalRejections = withdrawalRejections;
    this.withdrawalApproval = withdrawalApproval;
    this.rfiList = rfiList;
    this.rfiReplies = rfiReplies;
    this.rfiWithdrawals = rfiWithdrawals;
    this.delayNotification = delayNotification;
    this.stopNotification = stopNotification;
    this.informNotifications = informNotifications;
    this.outcomes = outcomes;
  }

  public Application getApplication() {
    return application;
  }

  public List<StatusUpdate> getStatusUpdates() {
    return statusUpdates;
  }

  public List<WithdrawalRequest> getWithdrawalRequests() {
    return withdrawalRequests;
  }

  public List<WithdrawalRejection> getWithdrawalRejections() {
    return withdrawalRejections;
  }

  public WithdrawalApproval getWithdrawalApproval() {
    return withdrawalApproval;
  }

  public List<Rfi> getRfiList() {
    return rfiList;
  }

  public List<RfiReply> getRfiReplies() {
    return rfiReplies;
  }

  public List<RfiWithdrawal> getRfiWithdrawals() {
    return rfiWithdrawals;
  }

  public Notification getDelayNotification() {
    return delayNotification;
  }

  public Notification getStopNotification() {
    return stopNotification;
  }

  public List<Notification> getInformNotifications() {
    return informNotifications;
  }

  public List<Outcome> getOutcomes() {
    return outcomes;
  }

}

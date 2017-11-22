package models;

import java.util.List;

public class AppData {

  private final Application application;
  private final String caseReference;
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
  private final Outcome outcome;
  private final List<AmendmentRequest> amendmentRequests;
  private final List<CaseData> caseDataList;

  public AppData(Application application, String caseReference, List<StatusUpdate> statusUpdates, List<WithdrawalRequest> withdrawalRequests, List<WithdrawalRejection> withdrawalRejections, WithdrawalApproval withdrawalApproval, List<Rfi> rfiList, List<RfiReply> rfiReplies, List<RfiWithdrawal> rfiWithdrawals, Notification delayNotification, Notification stopNotification, List<Notification> informNotifications, Outcome outcome, List<AmendmentRequest> amendmentRequests, List<CaseData> caseDataList) {
    this.application = application;
    this.caseReference = caseReference;
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
    this.outcome = outcome;
    this.amendmentRequests = amendmentRequests;
    this.caseDataList = caseDataList;
  }

  public Application getApplication() {
    return application;
  }

  public String getCaseReference() {
    return caseReference;
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

  public Outcome getOutcome() {
    return outcome;
  }

  public List<AmendmentRequest> getAmendmentRequests() {
    return amendmentRequests;
  }

  public List<CaseData> getCaseDataList() {
    return caseDataList;
  }

}

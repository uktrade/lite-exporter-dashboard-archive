package models;

import java.util.List;

public class CaseData {

  private final CaseDetails caseDetails;
  private final List<Rfi> rfiList;
  private final List<RfiReply> rfiReplies;
  private final List<RfiWithdrawal> rfiWithdrawals;
  private final List<Notification> informNotifications;
  private final Notification stopNotification;
  private final Outcome outcome;

  public CaseData(CaseDetails caseDetails,
                  List<Rfi> rfiList,
                  List<RfiReply> rfiReplies,
                  List<RfiWithdrawal> rfiWithdrawals,
                  List<Notification> informNotifications,
                  Notification stopNotification,
                  Outcome outcome) {
    this.caseDetails = caseDetails;
    this.rfiList = rfiList;
    this.rfiReplies = rfiReplies;
    this.rfiWithdrawals = rfiWithdrawals;
    this.informNotifications = informNotifications;
    this.stopNotification = stopNotification;
    this.outcome = outcome;
  }

  public CaseDetails getCaseDetails() {
    return caseDetails;
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

  public List<Notification> getInformNotifications() {
    return informNotifications;
  }

  public Notification getStopNotification() {
    return stopNotification;
  }

  public Outcome getOutcome() {
    return outcome;
  }

}

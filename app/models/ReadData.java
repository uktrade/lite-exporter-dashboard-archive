package models;

import java.util.Set;

public class ReadData {

  private final String unreadDelayNotificationId;
  private final String unreadStopNotificationId;
  private final Set<String> unreadInformNotificationIds;
  private final Set<String> unreadOutcomeIds;
  private final Set<String> unreadWithdrawalRejectionIds;
  private final Set<String> unreadRfiWithdrawalIds;

  public ReadData(String unreadDelayNotificationId,
                  String unreadStopNotificationId,
                  Set<String> unreadInformNotificationIds,
                  Set<String> unreadOutcomeIds,
                  Set<String> unreadWithdrawalRejectionIds,
                  Set<String> unreadRfiWithdrawalIds) {
    this.unreadDelayNotificationId = unreadDelayNotificationId;
    this.unreadStopNotificationId = unreadStopNotificationId;
    this.unreadInformNotificationIds = unreadInformNotificationIds;
    this.unreadOutcomeIds = unreadOutcomeIds;
    this.unreadWithdrawalRejectionIds = unreadWithdrawalRejectionIds;
    this.unreadRfiWithdrawalIds = unreadRfiWithdrawalIds;
  }

  public String getUnreadDelayNotificationId() {
    return unreadDelayNotificationId;
  }

  public String getUnreadStopNotificationId() {
    return unreadStopNotificationId;
  }

  public Set<String> getUnreadInformNotificationIds() {
    return unreadInformNotificationIds;
  }

  public Set<String> getUnreadOutcomeIds() {
    return unreadOutcomeIds;
  }

  public Set<String> getUnreadWithdrawalRejectionIds() {
    return unreadWithdrawalRejectionIds;
  }

  public Set<String> getUnreadRfiWithdrawalIds() {
    return unreadRfiWithdrawalIds;
  }

}

package uk.gov.bis.lite.exporterdashboard.api;

public enum RoutingKey {

  RFI_REPLY("rfi.reply"),
  WITHDRAWAL_REQUEST_CREATE("withdrawalrequest.create"),
  WITHDRAWAL_REQUEST_ACCEPT_READ("withdrawalrequest.accept.read"),
  AMENDMENT_CREATE("amendment.create"),
  NOTIFICATION_READ("notification.read"),
  OUTCOME_READ("outcome.read"),
  RFI_WITHDRAWAL_READ("rfi.withdrawal.read");

  private final String text;

  RoutingKey(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

package components.message;

public enum ConsumerRoutingKey {

  STATUS_UPDATE("status.update"),
  RFI("rfi.create"),
  RFI_WITHDRAWAL("rfi.withdrawal.create"),
  DELAY_NOTIFICATION("notification.delay"),
  STOP_NOTIFICATION("notification.stop"),
  INFORM_NOTIFICATION("notification.inform"),
  OUTCOME_ISSUE("outcome.issue"),
  OUTCOME_AMEND("outcome.amend"),
  WITHDRAWAL_REJECTION("withdrawalrequest.reject"),
  WITHDRAWAL_ACCEPT("withdrawalrequest.accept"),
  CASE_CREATE("case.create"),
  SIEL_SUBMIT("siel.submit");

  private final String text;

  ConsumerRoutingKey(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

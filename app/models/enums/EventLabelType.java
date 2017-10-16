package models.enums;

public enum EventLabelType {

  RFI("rfi"),
  STOPPED("stopped"),
  WITHDRAWAL_ACCEPTED("withdrawal-rejected"),
  WITHDRAWAL_REQUESTED("withdrawal-rejected"),
  WITHDRAWAL_REJECTED("withdrawal-rejected"),
  INFORM_ISSUED("inform-issued"),
  DELAYED("delayed"),
  DECISION("decision"),
  AMENDMENT_REQUESTED("amendment-requested");

  private final String text;

  EventLabelType(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

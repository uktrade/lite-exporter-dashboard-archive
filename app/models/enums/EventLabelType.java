package models.enums;

public enum EventLabelType {

  RFI("rfi"),
  STOPPED("stopped"),
  WITHDRAWAL_REQUESTED("withdrawal-rejected"),
  WITHDRAWAL_REJECTED("withdrawal-rejected"),
  AMENDMENT_REQUESTED("decision"),
  INFORM_ISSUED("inform-issued"),
  DELAYED("delayed"),
  DECISION("decision"),
  AMENDMENT_REQUEST("amendment-request");

  private final String text;

  EventLabelType(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

package models.enums;

public enum EventLabelType {

  RFI("rfi"),
  STOPPED("stopped"),
  WITHDRAWL_REJECTED("withdrawl-rejected"),
  INFORM_ISSUED("inform-issued"),
  DELAYED("delayed"),
  DECISION("decision");

  private final String text;

  EventLabelType(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

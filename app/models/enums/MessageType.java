package models.enums;

public enum MessageType {

  WITHDRAWAL_APPROVED("withdrawal-approved"),
  WITHDRAWAL_REJECTED("withdrawal-rejected"),
  WITHDRAWAL_REQUESTED("withdrawal-requested"),
  AMENDMENT("amendment"),
  STOPPED("stopped"),
  DELAYED("delayed");

  private final String text;

  MessageType(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

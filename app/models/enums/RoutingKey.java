package models.enums;

public enum RoutingKey {

  RFI_REPLY("rfi.reply"), WITHDRAW_REQUEST_CREATE("withdrawrequest.create"), AMEND_CREATE("amend.create");

  private final String text;

  RoutingKey(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

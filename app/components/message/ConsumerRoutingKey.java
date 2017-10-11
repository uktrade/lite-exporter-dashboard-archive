package components.message;

public enum ConsumerRoutingKey {

  STATUS_UPDATE("status.update"), RFI_CREATE("rfi.create");

  private final String text;

  ConsumerRoutingKey(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

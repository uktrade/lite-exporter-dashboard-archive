package models.view;

public class AddRfiResponseView {

  private final String sentAt;
  private final String rfiId;

  public AddRfiResponseView(String sentAt, String rfiId) {
    this.sentAt = sentAt;
    this.rfiId = rfiId;
  }

  public String getSentAt() {
    return sentAt;
  }

  public String getRfiId() {
    return rfiId;
  }

}

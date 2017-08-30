package models.view;

public class RfiResponseView {

  private final String sentBy;
  private final String sentAt;
  private final String message;

  public RfiResponseView(String sentBy, String sentAt, String message) {
    this.sentBy = sentBy;
    this.sentAt = sentAt;
    this.message = message;
  }

  public String getSentBy() {
    return sentBy;
  }

  public String getSentAt() {
    return sentAt;
  }

  public String getMessage() {
    return message;
  }

}

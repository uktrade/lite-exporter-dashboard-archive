package models.view;

public class RfiResponseView {

  private final String sentBy;
  private final String sentAt;
  private final String message;
  private final boolean editable;

  public RfiResponseView(String sentBy, String sentAt, String message, boolean editable) {
    this.sentBy = sentBy;
    this.sentAt = sentAt;
    this.message = message;
    this.editable = editable;
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

  public boolean isEditable() {
    return editable;
  }
}

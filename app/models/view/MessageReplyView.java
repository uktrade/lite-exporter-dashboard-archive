package models.view;

public class MessageReplyView {

  private final String title;
  private final String sender;
  private final String sentOn;
  private final String message;

  public MessageReplyView(String title, String sender, String sentOn, String message) {
    this.title = title;
    this.sender = sender;
    this.sentOn = sentOn;
    this.message = message;
  }

  public String getTitle() {
    return title;
  }

  public String getSender() {
    return sender;
  }

  public String getSentOn() {
    return sentOn;
  }

  public String getMessage() {
    return message;
  }

}

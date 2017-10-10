package models.view;

public class MessageReplyView {

  private final String anchor;
  private final String title;
  private final String sender;
  private final String sentOn;
  private final String message;
  private final boolean showNewIndicator;

  public MessageReplyView(String anchor, String title, String sender, String sentOn, String message, boolean showNewIndicator) {
    this.anchor = anchor;
    this.title = title;
    this.sender = sender;
    this.sentOn = sentOn;
    this.message = message;
    this.showNewIndicator = showNewIndicator;
  }

  public String getAnchor() {
    return anchor;
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

  public boolean isShowNewIndicator() {
    return showNewIndicator;
  }

}

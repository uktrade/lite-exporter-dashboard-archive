package models.view;

public class RfiView {

  private final String appId;
  private final String rfiId;
  private final String receivedOn;
  private final String replyBy;
  private final String sender;
  private final String message;
  private final RfiResponseView rfiResponseView;

  public RfiView(String appId, String rfiId, String receivedOn, String replyBy, String sender, String message, RfiResponseView rfiResponseView) {
    this.appId = appId;
    this.rfiId = rfiId;
    this.receivedOn = receivedOn;
    this.replyBy = replyBy;
    this.sender = sender;
    this.message = message;
    this.rfiResponseView = rfiResponseView;
  }

  public String getAppId() {
    return appId;
  }

  public String getRfiId() {
    return rfiId;
  }

  public String getReceivedOn() {
    return receivedOn;
  }

  public String getReplyBy() {
    return replyBy;
  }

  public String getSender() {
    return sender;
  }

  public String getMessage() {
    return message;
  }

  public RfiResponseView getRfiResponseView() {
    return rfiResponseView;
  }

}


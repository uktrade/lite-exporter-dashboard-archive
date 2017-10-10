package models.view;

public class RfiView {

  private final String appId;
  private final String rfiId;
  private final String receivedDate;
  private final String replyBy;
  private final String sender;
  private final String message;
  private final String withdrawnDate;
  private final boolean showNewIndicator;
  private final RfiReplyView rfiReplyView;

  public RfiView(String appId, String rfiId, String receivedDate, String replyBy, String sender, String message, String withdrawnDate, boolean showNewIndicator, RfiReplyView rfiReplyView) {
    this.appId = appId;
    this.rfiId = rfiId;
    this.receivedDate = receivedDate;
    this.replyBy = replyBy;
    this.sender = sender;
    this.message = message;
    this.withdrawnDate = withdrawnDate;
    this.showNewIndicator = showNewIndicator;
    this.rfiReplyView = rfiReplyView;
  }

  public String getAppId() {
    return appId;
  }

  public String getRfiId() {
    return rfiId;
  }

  public String getReceivedDate() {
    return receivedDate;
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

  public String getWithdrawnDate() {
    return withdrawnDate;
  }

  public boolean isShowNewIndicator() {
    return showNewIndicator;
  }

  public RfiReplyView getRfiReplyView() {
    return rfiReplyView;
  }

}

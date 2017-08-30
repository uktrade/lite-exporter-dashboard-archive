package models;

public class Amendment {

  private final String amendmentId;
  private final String appId;
  private final Long sentTimestamp;
  private final String sentBy;
  private final String message;
  private final String attachments;

  public Amendment(String amendmentId, String appId, Long sentTimestamp, String sentBy, String message, String attachments) {
    this.amendmentId = amendmentId;
    this.appId = appId;
    this.sentTimestamp = sentTimestamp;
    this.sentBy = sentBy;
    this.message = message;
    this.attachments = attachments;
  }

  public String getAmendmentId() {
    return amendmentId;
  }

  public String getAppId() {
    return appId;
  }

  public Long getSentTimestamp() {
    return sentTimestamp;
  }

  public String getSentBy() {
    return sentBy;
  }

  public String getMessage() {
    return message;
  }

  public String getAttachments() {
    return attachments;
  }

}

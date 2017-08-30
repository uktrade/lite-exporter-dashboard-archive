package models;

public class RfiResponse {

  private final String rfiId;
  private final String sentBy;
  private final Long sentTimestamp;
  private final String message;
  private final String attachments;

  public RfiResponse(String rfiId, String sentBy, Long sentTimestamp, String message, String attachments) {
    this.rfiId = rfiId;
    this.sentBy = sentBy;
    this.sentTimestamp = sentTimestamp;
    this.message = message;
    this.attachments = attachments;
  }

  public String getRfiId() {
    return rfiId;
  }

  public String getSentBy() {
    return sentBy;
  }

  public Long getSentTimestamp() {
    return sentTimestamp;
  }

  public String getMessage() {
    return message;
  }

  public String getAttachments() {
    return attachments;
  }

}

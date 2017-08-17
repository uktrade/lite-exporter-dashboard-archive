package models;

public class WithdrawalRequest {

  private final String withdrawalRequestId;
  private final String appId;
  private final Long sentTimestamp;
  private final String sentBy;
  private final String message;
  private final String attachments;
  private final String rejectedBy;
  private final Long rejectedTimestamp;
  private final String rejectedMessage;

  public WithdrawalRequest(String withdrawalRequestId, String appId, Long sentTimestamp, String sentBy, String message, String attachments, String rejectedBy, Long rejectedTimestamp, String rejectedMessage) {
    this.withdrawalRequestId = withdrawalRequestId;
    this.appId = appId;
    this.sentTimestamp = sentTimestamp;
    this.sentBy = sentBy;
    this.message = message;
    this.attachments = attachments;
    this.rejectedBy = rejectedBy;
    this.rejectedTimestamp = rejectedTimestamp;
    this.rejectedMessage = rejectedMessage;
  }

  public String getWithdrawalRequestId() {
    return withdrawalRequestId;
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

  public String getRejectedBy() {
    return rejectedBy;
  }

  public Long getRejectedTimestamp() {
    return rejectedTimestamp;
  }

  public String getRejectedMessage() {
    return rejectedMessage;
  }

}

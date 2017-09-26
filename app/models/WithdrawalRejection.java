package models;

public class WithdrawalRejection {

  private final String id;
  private final String appId;
  private final String createdByUserId;
  private final Long createdTimestamp;
  private final String message;

  public WithdrawalRejection(String id, String appId, String createdByUserId, Long createdTimestamp, String message) {
    this.id = id;
    this.appId = appId;
    this.createdByUserId = createdByUserId;
    this.createdTimestamp = createdTimestamp;
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public String getAppId() {
    return appId;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public String getMessage() {
    return message;
  }

}

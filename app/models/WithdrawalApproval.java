package models;

import java.util.List;

public class WithdrawalApproval implements RecipientMessage {

  private final String id;
  private final String appId;
  private final String createdByUserId;
  private final Long createdTimestamp;
  private final List<String> recipientUserIds;
  private final String message;

  public WithdrawalApproval(String id, String appId, String createdByUserId, Long createdTimestamp, List<String> recipientUserIds, String message) {
    this.id = id;
    this.appId = appId;
    this.createdByUserId = createdByUserId;
    this.createdTimestamp = createdTimestamp;
    this.recipientUserIds = recipientUserIds;
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

  public List<String> getRecipientUserIds() {
    return recipientUserIds;
  }

  public String getMessage() {
    return message;
  }

}

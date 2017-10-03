package models;

import java.util.List;

public class RfiWithdrawal {

  private final String id;
  private final String rfiId;
  private final String createdByUserId;
  private final Long createdTimestamp;
  private final List<String> recipientUserIds;
  private final String message;

  public RfiWithdrawal(String id, String rfiId, String createdByUserId, Long createdTimestamp, List<String> recipientUserIds, String message) {
    this.id = id;
    this.rfiId = rfiId;
    this.createdByUserId = createdByUserId;
    this.createdTimestamp = createdTimestamp;
    this.recipientUserIds = recipientUserIds;
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public String getRfiId() {
    return rfiId;
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

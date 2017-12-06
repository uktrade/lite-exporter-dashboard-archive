package models;

import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

import javax.validation.constraints.NotNull;

public class WithdrawalApproval implements RecipientMessage {

  @NotBlank
  private final String id;
  @NotBlank
  private final String appId;
  @NotBlank
  private final String createdByUserId;
  @NotNull
  private final Long createdTimestamp;
  @NotNull
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

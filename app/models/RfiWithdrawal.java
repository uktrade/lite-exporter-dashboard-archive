package models;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

public class RfiWithdrawal implements RecipientMessage {

  @NotBlank
  private final String id;
  @NotBlank
  private final String rfiId;
  @NotBlank
  private final String createdByUserId;
  @NotNull
  private final Long createdTimestamp;
  @NotNull
  private final List<String> recipientUserIds;
  @NotBlank
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

package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import models.enums.RfiStatus;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

public class Rfi {

  @NotBlank
  private final String id;
  @NotBlank
  private final String appId;
  @NotNull
  private final RfiStatus rfiStatus;
  @NotNull
  private final Long createdTimestamp;
  @NotNull
  private final Long dueTimestamp;
  @NotBlank
  private final String sentBy;
  @NotEmpty
  private final List<String> recipientUserIds;
  @NotBlank
  private final String message;

  public Rfi(@JsonProperty("id") String id,
             @JsonProperty("appId") String appId,
             @JsonProperty("rfiStatus") RfiStatus rfiStatus,
             @JsonProperty("createdTimestamp") Long createdTimestamp,
             @JsonProperty("dueTimestamp") Long dueTimestamp,
             @JsonProperty("sentBy") String sentBy,
             @JsonProperty("recipientUserIds") List<String> recipientUserIds,
             @JsonProperty("message") String message) {
    this.id = id;
    this.appId = appId;
    this.rfiStatus = rfiStatus;
    this.createdTimestamp = createdTimestamp;
    this.dueTimestamp = dueTimestamp;
    this.sentBy = sentBy;
    this.recipientUserIds = recipientUserIds;
    this.message = message;
  }

  public String getId() {
    return id;
  }

  public String getAppId() {
    return appId;
  }

  public RfiStatus getRfiStatus() {
    return rfiStatus;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public Long getDueTimestamp() {
    return dueTimestamp;
  }

  public String getSentBy() {
    return sentBy;
  }

  public List<String> getRecipientUserIds() {
    return recipientUserIds;
  }

  public String getMessage() {
    return message;
  }

}

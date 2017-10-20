package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import models.enums.StatusType;
import org.hibernate.validator.constraints.NotBlank;

public class StatusUpdate {

  @NotBlank
  private final String id;
  @NotBlank
  private final String appId;
  @NotNull
  private final StatusType statusType;
  @NotNull
  private final Long createdTimestamp;

  public StatusUpdate(@JsonProperty("id") String id,
                      @JsonProperty("appId") String appId,
                      @JsonProperty("statusType") StatusType statusType,
                      @JsonProperty("createdTimestamp") Long createdTimestamp) {
    this.id = id;
    this.appId = appId;
    this.statusType = statusType;
    this.createdTimestamp = createdTimestamp;
  }

  public String getId() {
    return id;
  }

  public String getAppId() {
    return appId;
  }

  public StatusType getStatusType() {
    return statusType;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

}

package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.enums.StatusType;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class StatusUpdate {

  @NotBlank
  private final String appId;
  @NotNull
  private final StatusType statusType;
  @NotNull
  private final Long startTimestamp;
  private final Long endTimestamp;

  public StatusUpdate(@JsonProperty("appId") String appId,
                      @JsonProperty("statusType") StatusType statusType,
                      @JsonProperty("startTimestamp") Long startTimestamp,
                      @JsonProperty("endTimestamp") Long endTimestamp) {
    this.appId = appId;
    this.statusType = statusType;
    this.startTimestamp = startTimestamp;
    this.endTimestamp = endTimestamp;
  }

  public String getAppId() {
    return appId;
  }

  public StatusType getStatusType() {
    return statusType;
  }

  public Long getStartTimestamp() {
    return startTimestamp;
  }

  public Long getEndTimestamp() {
    return endTimestamp;
  }

}

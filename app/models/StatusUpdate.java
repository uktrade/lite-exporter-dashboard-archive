package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.enums.StatusType;

public class StatusUpdate {

  private final String id;
  private final String appId;
  private final StatusType statusType;
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

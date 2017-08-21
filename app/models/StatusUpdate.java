package models;

import models.enums.StatusType;

public class StatusUpdate {

  private final String appId;
  private final StatusType statusType;
  private final Long startTimestamp;
  private final Long endTimestamp;

  public StatusUpdate(String appId, StatusType statusType, Long startTimestamp, Long endTimestamp) {
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

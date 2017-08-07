package models;

import models.enums.RfiStatus;

public class Rfi {

  private final String rfiId;
  private final String appId;
  private final RfiStatus rfiStatus;
  private final Long receivedTimestamp;
  private final Long dueTimestamp;
  private final String sentBy;
  private final String message;

  public Rfi(String rfiId, String appId, RfiStatus rfiStatus, Long receivedTimestamp, Long dueTimestamp, String sentBy, String message) {
    this.rfiId = rfiId;
    this.appId = appId;
    this.rfiStatus = rfiStatus;
    this.receivedTimestamp = receivedTimestamp;
    this.dueTimestamp = dueTimestamp;
    this.sentBy = sentBy;
    this.message = message;
  }

  public String getRfiId() {
    return rfiId;
  }

  public String getAppId() {
    return appId;
  }

  public RfiStatus getRfiStatus() {
    return rfiStatus;
  }

  public Long getReceivedTimestamp() {
    return receivedTimestamp;
  }

  public Long getDueTimestamp() {
    return dueTimestamp;
  }

  public String getSentBy() {
    return sentBy;
  }

  public String getMessage() {
    return message;
  }

}

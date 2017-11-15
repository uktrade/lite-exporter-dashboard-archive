package models;

public class StatusColumnInfo {

  private final String prefix;
  private final Long applicationStatusTimestamp;
  private final String applicationStatus;

  public StatusColumnInfo(String prefix, Long applicationStatusTimestamp, String applicationStatus) {
    this.prefix = prefix;
    this.applicationStatusTimestamp = applicationStatusTimestamp;
    this.applicationStatus = applicationStatus;
  }

  public String getPrefix() {
    return prefix;
  }

  public Long getApplicationStatusTimestamp() {
    return applicationStatusTimestamp;
  }

  public String getApplicationStatus() {
    return applicationStatus;
  }

}

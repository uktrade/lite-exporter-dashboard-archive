package models;

public class DateColumnInfo {

  private final String dateStatus;
  private final Long dateTimestamp;

  public DateColumnInfo(String dateStatus, Long dateTimestamp) {
    this.dateStatus = dateStatus;
    this.dateTimestamp = dateTimestamp;
  }

  public String getDateStatus() {
    return dateStatus;
  }

  public Long getDateTimestamp() {
    return dateTimestamp;
  }
  
}

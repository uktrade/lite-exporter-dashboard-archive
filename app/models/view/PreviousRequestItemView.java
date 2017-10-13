package models.view;

public class PreviousRequestItemView {

  private final Long createdTimestamp;
  private final String date;
  private final String type;
  private final String link;
  private final String indicator;

  public PreviousRequestItemView(Long createdTimestamp, String date, String type, String link, String indicator) {
    this.createdTimestamp = createdTimestamp;
    this.date = date;
    this.type = type;
    this.link = link;
    this.indicator = indicator;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public String getDate() {
    return date;
  }

  public String getType() {
    return type;
  }

  public String getLink() {
    return link;
  }

  public String getIndicator() {
    return indicator;
  }

}

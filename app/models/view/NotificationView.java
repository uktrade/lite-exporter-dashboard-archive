package models.view;

import models.enums.EventLabelType;

public class NotificationView {

  private final EventLabelType eventLabelType;
  private final String linkText;
  private final String link;
  private final String description;
  private final Long createdTimestamp;

  public NotificationView(EventLabelType eventLabelType, String linkText, String link, String description, Long createdTimestamp) {
    this.eventLabelType = eventLabelType;
    this.linkText = linkText;
    this.link = link;
    this.description = description;
    this.createdTimestamp = createdTimestamp;
  }

  public EventLabelType getEventLabelType() {
    return eventLabelType;
  }

  public String getLinkText() {
    return linkText;
  }

  public String getLink() {
    return link;
  }

  public String getDescription() {
    return description;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

}

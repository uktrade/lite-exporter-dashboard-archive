package models.view;

import models.enums.EventLabelType;

import java.util.List;

public class MessageView {

  private final EventLabelType eventLabelType;
  private final String title;
  private final String receivedOn;
  private final String sentOn;
  private final String sender;
  private final String message;
  private final Long createdTimestamp;
  private final List<FileView> fileViews;
  private final MessageReplyView messageReplyView;

  public MessageView(EventLabelType eventLabelType,
                     String title,
                     String receivedOn,
                     String sentOn,
                     String sender,
                     String message,
                     Long createdTimestamp,
                     List<FileView> fileViews,
                     MessageReplyView messageReplyView) {
    this.eventLabelType = eventLabelType;
    this.title = title;
    this.receivedOn = receivedOn;
    this.sentOn = sentOn;
    this.sender = sender;
    this.message = message;
    this.createdTimestamp = createdTimestamp;
    this.fileViews = fileViews;
    this.messageReplyView = messageReplyView;
  }

  public EventLabelType getEventLabelType() {
    return eventLabelType;
  }

  public String getTitle() {
    return title;
  }

  public String getReceivedOn() {
    return receivedOn;
  }

  public String getSentOn() {
    return sentOn;
  }

  public String getSender() {
    return sender;
  }

  public String getMessage() {
    return message;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public List<FileView> getFileViews() {
    return fileViews;
  }

  public MessageReplyView getMessageReplyView() {
    return messageReplyView;
  }

}

package models.view;

import components.common.upload.FileView;

import java.util.List;

public class RfiReplyView {

  private final String sentBy;
  private final String sentAt;
  private final String message;
  private final List<FileView> fileViews;

  public RfiReplyView(String sentBy, String sentAt, String message, List<FileView> fileViews) {
    this.sentBy = sentBy;
    this.sentAt = sentAt;
    this.message = message;
    this.fileViews = fileViews;
  }

  public String getSentBy() {
    return sentBy;
  }

  public String getSentAt() {
    return sentAt;
  }

  public String getMessage() {
    return message;
  }

  public List<FileView> getFileViews() {
    return fileViews;
  }

}

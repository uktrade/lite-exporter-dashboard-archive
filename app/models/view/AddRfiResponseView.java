package models.view;

import java.util.List;

public class AddRfiResponseView {

  private final String sentAt;
  private final String rfiId;
  private final List<FileView> fileViews;

  public AddRfiResponseView(String sentAt, String rfiId, List<FileView> fileViews) {
    this.sentAt = sentAt;
    this.rfiId = rfiId;
    this.fileViews = fileViews;
  }

  public String getSentAt() {
    return sentAt;
  }

  public String getRfiId() {
    return rfiId;
  }

  public List<FileView> getFileViews() {
    return fileViews;
  }

}

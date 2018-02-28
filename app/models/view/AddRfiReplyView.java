package models.view;

import components.common.upload.FileView;

import java.util.List;

public class AddRfiReplyView {

  private final String rfiId;
  private final List<FileView> fileViews;

  public AddRfiReplyView(String rfiId, List<FileView> fileViews) {
    this.rfiId = rfiId;
    this.fileViews = fileViews;
  }

  public String getRfiId() {
    return rfiId;
  }

  public List<FileView> getFileViews() {
    return fileViews;
  }

}

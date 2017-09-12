package models.view;

import models.enums.DraftType;

import java.util.List;

public class AddAmendmentView {

  private final DraftType draftType;

  private final List<FileView> fileViews;

  public AddAmendmentView(DraftType draftType, List<FileView> fileViews) {
    this.draftType = draftType;
    this.fileViews = fileViews;
  }

  public DraftType getDraftType() {
    return draftType;
  }

  public List<FileView> getFileViews() {
    return fileViews;
  }
}

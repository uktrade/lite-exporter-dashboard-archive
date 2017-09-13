package models;

import models.enums.DraftType;

import java.util.List;

public class Draft {

  private final String relatedId;
  private final DraftType draftType;
  private final List<File> attachments;

  public Draft(String relatedId, DraftType draftType, List<File> attachments) {
    this.draftType = draftType;
    this.relatedId = relatedId;
    this.attachments = attachments;
  }

  public DraftType getDraftType() {
    return draftType;
  }

  public String getRelatedId() {
    return relatedId;
  }

  public List<File> getAttachments() {
    return attachments;
  }
}

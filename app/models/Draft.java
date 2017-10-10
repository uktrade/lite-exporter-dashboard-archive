package models;

import java.util.List;
import models.enums.DraftType;
import uk.gov.bis.lite.exporterdashboard.api.File;

public class Draft {

  private final String id;
  private final String relatedId;
  private final DraftType draftType;
  private final List<File> attachments;

  public Draft(String id, String relatedId, DraftType draftType, List<File> attachments) {
    this.id = id;
    this.relatedId = relatedId;
    this.draftType = draftType;
    this.attachments = attachments;
  }

  public String getId() {
    return id;
  }

  public String getRelatedId() {
    return relatedId;
  }

  public DraftType getDraftType() {
    return draftType;
  }

  public List<File> getAttachments() {
    return attachments;
  }

}

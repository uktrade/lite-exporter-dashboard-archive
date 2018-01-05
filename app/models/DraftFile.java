package models;

import models.enums.DraftType;

public class DraftFile {

  private final String id;
  private final String filename;
  private final String url;
  private final String relatedId;
  private final DraftType draftType;

  public DraftFile(String id,
                   String filename,
                   String url,
                   String relatedId,
                   DraftType draftType) {
    this.id = id;
    this.filename = filename;
    this.url = url;
    this.relatedId = relatedId;
    this.draftType = draftType;
  }

  public String getId() {
    return id;
  }

  public String getFilename() {
    return filename;
  }

  public String getUrl() {
    return url;
  }

  public String getRelatedId() {
    return relatedId;
  }

  public DraftType getDraftType() {
    return draftType;
  }

}

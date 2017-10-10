package models;

import models.enums.ReadType;

public class Read {

  private final String id;
  private final String relatedId;
  private final ReadType readType;
  private final String createdByUserId;

  public Read(String id, String relatedId, ReadType readType, String createdByUserId) {
    this.id = id;
    this.relatedId = relatedId;
    this.readType = readType;
    this.createdByUserId = createdByUserId;
  }

  public String getId() {
    return id;
  }

  public String getRelatedId() {
    return relatedId;
  }

  public ReadType getReadType() {
    return readType;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

}

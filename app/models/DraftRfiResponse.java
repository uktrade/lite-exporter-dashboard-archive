package models;

import java.util.List;

public class DraftRfiResponse {

  private final String rfiId;
  private final List<File> attachments;

  public DraftRfiResponse(String rfiId, List<File> attachments) {
    this.rfiId = rfiId;
    this.attachments = attachments;
  }

  public String getRfiId() {
    return rfiId;
  }

  public List<File> getAttachments() {
    return attachments;
  }

}

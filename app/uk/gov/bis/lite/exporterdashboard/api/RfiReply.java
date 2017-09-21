package uk.gov.bis.lite.exporterdashboard.api;

import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

import javax.validation.constraints.NotNull;

public class RfiReply implements ExporterDashboardMessage {

  @NotBlank
  private String id;

  @NotBlank
  private String rfiId;

  @NotBlank
  private String createdByUserId;

  @NotNull
  private Long createdTimestamp;

  @NotBlank
  private String message;

  @NotNull
  private List<File> attachments;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRfiId() {
    return rfiId;
  }

  public void setRfiId(String rfiId) {
    this.rfiId = rfiId;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public void setCreatedByUserId(String createdByUserId) {
    this.createdByUserId = createdByUserId;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Long createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<File> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<File> attachments) {
    this.attachments = attachments;
  }

}

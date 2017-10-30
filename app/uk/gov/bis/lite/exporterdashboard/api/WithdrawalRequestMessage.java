package uk.gov.bis.lite.exporterdashboard.api;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

public class WithdrawalRequestMessage implements ExporterDashboardMessage {

  @NotBlank
  private String id;

  @NotBlank
  private String appId;

  @NotBlank
  private String createdByUserId;

  @NotNull
  private Long createdTimestamp;

  @NotBlank
  private String message;

  @NotNull
  private List<DashboardDocument> attachments;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
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

  public List<DashboardDocument> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<DashboardDocument> attachments) {
    this.attachments = attachments;
  }

}

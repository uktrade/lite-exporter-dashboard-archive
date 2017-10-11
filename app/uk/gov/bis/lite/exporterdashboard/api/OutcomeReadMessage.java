package uk.gov.bis.lite.exporterdashboard.api;

import org.hibernate.validator.constraints.NotBlank;

public class OutcomeReadMessage implements ExporterDashboardMessage {

  @NotBlank
  private String outcomeId;

  @NotBlank
  private String appId;

  @NotBlank
  private String createdByUserId;

  public String getOutcomeId() {
    return outcomeId;
  }

  public void setOutcomeId(String outcomeId) {
    this.outcomeId = outcomeId;
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

}

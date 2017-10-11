package uk.gov.bis.lite.exporterdashboard.api;

import org.hibernate.validator.constraints.NotBlank;

public class RfiWithdrawalReadMessage implements ExporterDashboardMessage {

  @NotBlank
  private String appId;

  @NotBlank
  private String rfiId;

  @NotBlank
  private String createdByUserId;

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
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

}

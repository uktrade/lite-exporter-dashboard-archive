package uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardRfiDeadlineUpdate implements SpireToDashboardMessage {

  @NotEmpty
  private String appId;

  @NotEmpty
  private String rfiId;

  @NotEmpty
  private String createdByUserId;

  @NotNull
  private Long updatedDeadlineTimestamp;

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

  public String getRfiId() {
    return rfiId;
  }

  public void setRfiId(String rfiId) {
    this.rfiId = rfiId;
  }

  public Long getUpdatedDeadlineTimestamp() {
    return updatedDeadlineTimestamp;
  }

  public void setUpdatedDeadlineTimestamp(Long updatedDeadlineTimestamp) {
    this.updatedDeadlineTimestamp = updatedDeadlineTimestamp;
  }

}

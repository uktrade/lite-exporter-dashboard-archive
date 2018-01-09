package uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardRfiDeadlineReminder implements SpireToDashboardMessage {

  @NotEmpty
  private String appId;

  @NotEmpty
  private String rfiId;

  @NotNull
  private Long deadlineTimestamp;

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

  public Long getDeadlineTimestamp() {
    return deadlineTimestamp;
  }

  public void setDeadlineTimestamp(Long deadlineTimestamp) {
    this.deadlineTimestamp = deadlineTimestamp;
  }

}

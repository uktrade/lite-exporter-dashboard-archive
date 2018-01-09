package uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardCaseStatusUpdate implements SpireToDashboardMessage {

  @NotEmpty
  private String appId;

  @NotEmpty
  private String caseRef;

  @NotNull
  private DashboardStatusCode statusCode;

  @NotNull
  private Long createdTimestamp;

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getCaseRef() {
    return caseRef;
  }

  public void setCaseRef(String caseRef) {
    this.caseRef = caseRef;
  }

  public DashboardStatusCode getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(DashboardStatusCode statusCode) {
    this.statusCode = statusCode;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Long createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

}

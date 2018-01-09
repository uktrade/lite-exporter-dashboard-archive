package uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardOfficerUpdate implements SpireToDashboardMessage {

  @NotEmpty
  private String appId;

  @NotEmpty
  private String caseRef;

  @NotEmpty
  private String caseOfficerId;

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

  public String getCaseOfficerId() {
    return caseOfficerId;
  }

  public void setCaseOfficerId(String caseOfficerId) {
    this.caseOfficerId = caseOfficerId;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Long createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

}

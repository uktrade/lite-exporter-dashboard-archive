package models;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

public class CaseDetails {

  @NotBlank
  private final String appId;

  @NotBlank
  private final String caseReference;

  private final String createdByUserId;

  @NotNull
  private final Long createdTimestamp;

  public CaseDetails(String appId, String caseReference, String createdByUserId, Long createdTimestamp) {
    this.appId = appId;
    this.caseReference = caseReference;
    this.createdByUserId = createdByUserId;
    this.createdTimestamp = createdTimestamp;
  }

  public String getAppId() {
    return appId;
  }

  public String getCaseReference() {
    return caseReference;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

}

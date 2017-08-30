package models;

import java.util.List;

public class Application {

  private final String appId;
  private final String companyId;
  private final String createdBy;
  private final Long createdTimestamp;
  private final Long submittedTimestamp;
  private final List<String> destinationList;
  private final String applicantReference;
  private final String caseReference;
  private final String caseOfficerId;

  public Application(String appId, String companyId, String createdBy, Long createdTimestamp, Long submittedTimestamp, List<String> destinationList, String applicantReference, String caseReference, String caseOfficerId) {
    this.appId = appId;
    this.companyId = companyId;
    this.createdBy = createdBy;
    this.createdTimestamp = createdTimestamp;
    this.submittedTimestamp = submittedTimestamp;
    this.destinationList = destinationList;
    this.applicantReference = applicantReference;
    this.caseReference = caseReference;
    this.caseOfficerId = caseOfficerId;
  }

  public String getAppId() {
    return appId;
  }

  public String getCompanyId() {
    return companyId;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public Long getSubmittedTimestamp() {
    return submittedTimestamp;
  }

  public List<String> getDestinationList() {
    return destinationList;
  }

  public String getApplicantReference() {
    return applicantReference;
  }

  public String getCaseReference() {
    return caseReference;
  }

  public String getCaseOfficerId() {
    return caseOfficerId;
  }

}

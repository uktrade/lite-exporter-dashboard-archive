package models;

import models.enums.ApplicationStatus;

import java.util.List;

public class Application {

  private final String appId;
  private final String companyId;
  private final ApplicationStatus applicationStatus;
  private final String applicantReference;
  private final List<String> destinationList;
  private final String caseReference;
  private final String caseOfficerId;

  public Application(String appId, String companyId, ApplicationStatus applicationStatus, String applicantReference, List<String> destinationList, String caseReference, String caseOfficerId) {
    this.appId = appId;
    this.companyId = companyId;
    this.applicationStatus = applicationStatus;
    this.applicantReference = applicantReference;
    this.destinationList = destinationList;
    this.caseReference = caseReference;
    this.caseOfficerId = caseOfficerId;
  }

  public String getAppId() {
    return appId;
  }

  public String getCompanyId() {
    return companyId;
  }

  public ApplicationStatus getApplicationStatus() {
    return applicationStatus;
  }

  public String getApplicantReference() {
    return applicantReference;
  }

  public List<String> getDestinationList() {
    return destinationList;
  }

  public String getCaseReference() {
    return caseReference;
  }

  public String getCaseOfficerId() {
    return caseOfficerId;
  }
}

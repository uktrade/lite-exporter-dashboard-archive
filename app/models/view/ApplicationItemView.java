package models.view;

import models.enums.ApplicationProgress;

public class ApplicationItemView {

  private final String appId;
  private final String companyId;
  private final String companyName;
  private final String createdById;
  private final String createdByFirstName;
  private final String createdByLastName;
  private final Long dateTimestamp;
  private final String date;
  private final String caseReference;
  private final String applicantReference;
  private final ApplicationProgress applicationProgress;
  private final String applicationStatus;
  private final String applicationStatusDate;
  private final long applicationStatusTimestamp;
  private final String destination;
  private final String openRfiId;

  public ApplicationItemView(String appId,
                             String companyId,
                             String companyName,
                             String createdById,
                             String createdByFirstName,
                             String createdByLastName,
                             Long dateTimestamp,
                             String date,
                             String caseReference,
                             String applicantReference,
                             ApplicationProgress applicationProgress,
                             String applicationStatus,
                             String applicationStatusDate,
                             long applicationStatusTimestamp,
                             String destination,
                             String openRfiId) {
    this.appId = appId;
    this.companyId = companyId;
    this.companyName = companyName;
    this.createdById = createdById;
    this.createdByFirstName = createdByFirstName;
    this.createdByLastName = createdByLastName;
    this.dateTimestamp = dateTimestamp;
    this.date = date;
    this.caseReference = caseReference;
    this.applicantReference = applicantReference;
    this.applicationProgress = applicationProgress;
    this.applicationStatus = applicationStatus;
    this.applicationStatusDate = applicationStatusDate;
    this.applicationStatusTimestamp = applicationStatusTimestamp;
    this.destination = destination;
    this.openRfiId = openRfiId;
  }

  public String getAppId() {
    return appId;
  }

  public String getCompanyId() {
    return companyId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getCreatedById() {
    return createdById;
  }

  public String getCreatedByFirstName() {
    return createdByFirstName;
  }

  public String getCreatedByLastName() {
    return createdByLastName;
  }

  public Long getDateTimestamp() {
    return dateTimestamp;
  }

  public String getDate() {
    return date;
  }

  public String getCaseReference() {
    return caseReference;
  }

  public String getApplicantReference() {
    return applicantReference;
  }

  public ApplicationProgress getApplicationProgress() {
    return applicationProgress;
  }

  public String getApplicationStatus() {
    return applicationStatus;
  }

  public String getApplicationStatusDate() {
    return applicationStatusDate;
  }

  public long getApplicationStatusTimestamp() {
    return applicationStatusTimestamp;
  }

  public String getDestination() {
    return destination;
  }

  public String getOpenRfiId() {
    return openRfiId;
  }

}

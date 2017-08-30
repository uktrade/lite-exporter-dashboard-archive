package models.view;

import models.enums.StatusType;

public class ApplicationItemView {

  private final String appId;
  private final String companyId;
  private final String companyName;
  private final String createdById;
  private final String createdByName;
  private final Long createdTimestamp;
  private final Long submittedTimestamp;
  private final String date;
  private final String caseId;
  private final String caseDescription;
  private final StatusType statusType;
  private final String applicationStatus;
  private final String applicationStatusDate;
  private final long applicationStatusTimestamp;
  private final String destination;
  private final String openRfiId;

  public ApplicationItemView(String appId, String companyId, String companyName, String createdById, String createdByName, Long createdTimestamp, Long submittedTimestamp, String date, String caseId, String caseDescription, StatusType statusType, String applicationStatus, String applicationStatusDate, long applicationStatusTimestamp, String destination, String openRfiId) {
    this.appId = appId;
    this.companyId = companyId;
    this.companyName = companyName;
    this.createdById = createdById;
    this.createdByName = createdByName;
    this.createdTimestamp = createdTimestamp;
    this.submittedTimestamp = submittedTimestamp;
    this.date = date;
    this.caseId = caseId;
    this.caseDescription = caseDescription;
    this.statusType = statusType;
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

  public String getCreatedByName() {
    return createdByName;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public Long getSubmittedTimestamp() {
    return submittedTimestamp;
  }

  public String getDate() {
    return date;
  }

  public String getCaseId() {
    return caseId;
  }

  public String getCaseDescription() {
    return caseDescription;
  }

  public StatusType getStatusType() {
    return statusType;
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

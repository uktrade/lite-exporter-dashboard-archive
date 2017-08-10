package models.view;

import models.enums.StatusType;

public class ApplicationItemView {

  private final String appId;
  private final String companyId;
  private final String companyName;
  private final long dateSubmittedTimestamp;
  private final String dateSubmitted;
  private final String caseDescription;
  private final StatusType statusType;
  private final String applicationStatus;
  private final String applicationStatusDate;
  private final long applicationStatusTimestamp;
  private final String destination;
  private final String openRfiId;

  public ApplicationItemView(String appId, String companyId, String companyName, long dateSubmittedTimestamp, String dateSubmitted, String caseDescription, StatusType statusType, String applicationStatus, String applicationStatusDate, long applicationStatusTimestamp, String destination, String openRfiId) {
    this.appId = appId;
    this.companyId = companyId;
    this.companyName = companyName;
    this.dateSubmittedTimestamp = dateSubmittedTimestamp;
    this.dateSubmitted = dateSubmitted;
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

  public long getDateSubmittedTimestamp() {
    return dateSubmittedTimestamp;
  }

  public String getDateSubmitted() {
    return dateSubmitted;
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

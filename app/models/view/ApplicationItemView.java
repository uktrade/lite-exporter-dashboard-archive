package models.view;

import models.enums.StatusType;

public class ApplicationItemView {

  private final String appId;
  private final long dateSubmittedTimestamp;
  private final String dateSubmitted;
  private final String caseDescription;
  private final StatusType statusType;
  private final String applicationStatus;
  private final String applicationStatusDate;
  private final long applicationStatusTimestamp;
  private final String destination;

  public ApplicationItemView(String appId,
                             long dateSubmittedTimestamp,
                             String dateSubmitted,
                             String caseDescription,
                             StatusType statusType,
                             String applicationStatus,
                             String applicationStatusDate,
                             long applicationStatusTimestamp,
                             String destination) {
    this.appId = appId;
    this.dateSubmittedTimestamp = dateSubmittedTimestamp;
    this.dateSubmitted = dateSubmitted;
    this.caseDescription = caseDescription;
    this.statusType = statusType;
    this.applicationStatus = applicationStatus;
    this.applicationStatusDate = applicationStatusDate;
    this.applicationStatusTimestamp = applicationStatusTimestamp;
    this.destination = destination;
  }

  public String getAppId() {
    return appId;
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
}

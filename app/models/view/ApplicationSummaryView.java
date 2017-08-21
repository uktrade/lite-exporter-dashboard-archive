package models.view;

public class ApplicationSummaryView {

  private final String appId;
  private final String caseDescription;
  private final String destination;
  private final String dateSubmitted;
  private final String status;
  private final String caseOfficer;

  public ApplicationSummaryView(String appId, String caseDescription, String destination, String dateSubmitted, String status, String caseOfficer) {
    this.appId = appId;
    this.caseDescription = caseDescription;
    this.destination = destination;
    this.dateSubmitted = dateSubmitted;
    this.status = status;
    this.caseOfficer = caseOfficer;
  }

  public String getAppId() {
    return appId;
  }

  public String getCaseDescription() {
    return caseDescription;
  }

  public String getDestination() {
    return destination;
  }

  public String getDateSubmitted() {
    return dateSubmitted;
  }

  public String getStatus() {
    return status;
  }

  public String getCaseOfficer() {
    return caseOfficer;
  }

}

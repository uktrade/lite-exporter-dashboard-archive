package models.view;

public class ApplicationSummaryView {

  private final String caseReference;
  private final String destination;
  private final String dateSubmitted;
  private final String status;
  private final String caseOfficer;

  public ApplicationSummaryView(String caseReference, String destination, String dateSubmitted, String status, String caseOfficer) {
    this.caseReference = caseReference;
    this.destination = destination;
    this.dateSubmitted = dateSubmitted;
    this.status = status;
    this.caseOfficer = caseOfficer;
  }

  public String getCaseReference() {
    return caseReference;
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

package models.view;

public class ApplicationSummaryView {

  private final String appId;
  private final String caseReference;
  private final String applicantReference;
  private final String destination;
  private final String dateSubmitted;
  private final String status;
  private final String caseOfficer;
  private final String licenceApplicationLink;

  public ApplicationSummaryView(String appId,
                                String caseReference,
                                String applicantReference,
                                String destination,
                                String dateSubmitted,
                                String status,
                                String caseOfficer,
                                String licenceApplicationLink) {
    this.appId = appId;
    this.caseReference = caseReference;
    this.applicantReference = applicantReference;
    this.destination = destination;
    this.dateSubmitted = dateSubmitted;
    this.status = status;
    this.caseOfficer = caseOfficer;
    this.licenceApplicationLink = licenceApplicationLink;
  }

  public String getAppId() {
    return appId;
  }

  public String getCaseReference() {
    return caseReference;
  }

  public String getApplicantReference() {
    return applicantReference;
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

  public String getLicenceApplicationLink() {
    return licenceApplicationLink;
  }

}

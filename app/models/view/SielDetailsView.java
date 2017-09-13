package models.view;

public class SielDetailsView {

  private final String caseReference;
  private final String applicantReference;
  private final String licenceType;
  private final String sielStatus;
  private final String issueDate;
  private final String expiryDate;
  private final String exportDestinations;
  private final String site;
  private final String licensee;

  public SielDetailsView(String caseReference, String applicantReference, String licenceType, String sielStatus, String issueDate, String expiryDate, String exportDestinations, String site, String licensee) {
    this.caseReference = caseReference;
    this.applicantReference = applicantReference;
    this.licenceType = licenceType;
    this.sielStatus = sielStatus;
    this.issueDate = issueDate;
    this.expiryDate = expiryDate;
    this.exportDestinations = exportDestinations;
    this.site = site;
    this.licensee = licensee;
  }

  public String getCaseReference() {
    return caseReference;
  }

  public String getApplicantReference() {
    return applicantReference;
  }

  public String getLicenceType() {
    return licenceType;
  }

  public String getSielStatus() {
    return sielStatus;
  }

  public String getIssueDate() {
    return issueDate;
  }

  public String getExpiryDate() {
    return expiryDate;
  }

  public String getExportDestinations() {
    return exportDestinations;
  }

  public String getSite() {
    return site;
  }

  public String getLicensee() {
    return licensee;
  }

}

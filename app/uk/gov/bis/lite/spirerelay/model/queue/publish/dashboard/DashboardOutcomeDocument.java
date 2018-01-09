package uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard;

public class DashboardOutcomeDocument {

  private String id;

  private OutcomeDocumentType documentType;

  private String licenceRef;

  private String licenceExpiry;

  private String filename;

  private String url;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public OutcomeDocumentType getDocumentType() {
    return documentType;
  }

  public void setDocumentType(OutcomeDocumentType documentType) {
    this.documentType = documentType;
  }

  public String getLicenceRef() {
    return licenceRef;
  }

  public void setLicenceRef(String licenceRef) {
    this.licenceRef = licenceRef;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getLicenceExpiry() {
    return licenceExpiry;
  }

  public void setLicenceExpiry(String licenceExpiry) {
    this.licenceExpiry = licenceExpiry;
  }
}

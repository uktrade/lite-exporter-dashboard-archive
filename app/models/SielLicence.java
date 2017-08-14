package models;

public class SielLicence {

  private final String licenceId;
  private final String description;
  private final String licenseeId;
  private final String expiryTimestamp;

  public SielLicence(String licenceId, String description, String licenseeId, String expiryTimestamp) {
    this.licenceId = licenceId;
    this.description = description;
    this.licenseeId = licenseeId;
    this.expiryTimestamp = expiryTimestamp;
  }

  public String getLicenceId() {
    return licenceId;
  }

  public String getDescription() {
    return description;
  }

  public String getLicenseeId() {
    return licenseeId;
  }

  public String getExpiryTimestamp() {
    return expiryTimestamp;
  }
}

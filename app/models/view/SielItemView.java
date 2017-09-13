package models.view;

public class SielItemView {

  private final String registrationReference;
  private final String description;
  private final String licensee;
  private final String expiryDate;
  private final Long expiryTimestamp;
  private final String sielStatus;

  public SielItemView(String registrationReference, String description, String licensee, String expiryDate, Long expiryTimestamp, String sielStatus) {
    this.registrationReference = registrationReference;
    this.description = description;
    this.licensee = licensee;
    this.expiryDate = expiryDate;
    this.expiryTimestamp = expiryTimestamp;
    this.sielStatus = sielStatus;
  }

  public String getRegistrationReference() {
    return registrationReference;
  }

  public String getDescription() {
    return description;
  }

  public String getLicensee() {
    return licensee;
  }

  public String getExpiryDate() {
    return expiryDate;
  }

  public Long getExpiryTimestamp() {
    return expiryTimestamp;
  }

  public String getSielStatus() {
    return sielStatus;
  }

}

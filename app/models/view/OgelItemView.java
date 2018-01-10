package models.view;

public class OgelItemView {

  private final String registrationReference;
  private final String description;
  private final String licensee;
  private final String site;
  private final String registrationDate;
  private final long registrationTimestamp;
  private final String updatedDate;
  private final long updatedTimestamp;
  private final String ogelStatus;

  public OgelItemView(String registrationReference,
                      String description,
                      String licensee,
                      String site,
                      String registrationDate,
                      long registrationTimestamp,
                      String updatedDate,
                      long updatedTimestamp,
                      String ogelStatus) {
    this.registrationReference = registrationReference;
    this.description = description;
    this.licensee = licensee;
    this.site = site;
    this.registrationDate = registrationDate;
    this.registrationTimestamp = registrationTimestamp;
    this.updatedDate = updatedDate;
    this.updatedTimestamp = updatedTimestamp;
    this.ogelStatus = ogelStatus;
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

  public String getSite() {
    return site;
  }

  public String getRegistrationDate() {
    return registrationDate;
  }

  public long getRegistrationTimestamp() {
    return registrationTimestamp;
  }

  public String getUpdatedDate() {
    return updatedDate;
  }

  public long getUpdatedTimestamp() {
    return updatedTimestamp;
  }

  public String getOgelStatus() {
    return ogelStatus;
  }

}

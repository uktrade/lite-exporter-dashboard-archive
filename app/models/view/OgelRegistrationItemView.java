package models.view;

public class OgelRegistrationItemView {

  private final String registrationReference;
  private final String description;
  private final String licensee;
  private final String site;
  private final String registrationDate;

  public OgelRegistrationItemView(String registrationReference, String description, String licensee, String site, String registrationDate) {
    this.registrationReference = registrationReference;
    this.description = description;
    this.licensee = licensee;
    this.site = site;
    this.registrationDate = registrationDate;
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
}

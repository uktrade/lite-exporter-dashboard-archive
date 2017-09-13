package models.enums;

public enum LicenceSortType {

  STATUS("status"), REFERENCE("reference"), LICENSEE("licensee"), SITE("site"), REGISTRATION_DATE("registration"), EXPIRY_DATE("expiry");

  private final String text;

  LicenceSortType(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

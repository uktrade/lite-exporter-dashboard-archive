package models.enums;

public enum LicenceSortType {

  REFERENCE("reference"), LICENSEE("licensee"), SITE("site"), DATE("date");

  private final String text;

  LicenceSortType(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

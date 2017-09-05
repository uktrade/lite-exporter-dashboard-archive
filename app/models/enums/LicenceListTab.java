package models.enums;

public enum LicenceListTab {

  OGELS("ogels"), SIELS("siels");

  private final String text;

  LicenceListTab(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

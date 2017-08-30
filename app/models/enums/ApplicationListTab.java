package models.enums;

public enum ApplicationListTab {

  USER("user"), COMPANY("company");

  private final String text;

  ApplicationListTab(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

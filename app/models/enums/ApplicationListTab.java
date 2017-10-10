package models.enums;

public enum ApplicationListTab {

  USER("user"), COMPANY("company"), ATTENTION("attention");

  private final String text;

  ApplicationListTab(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

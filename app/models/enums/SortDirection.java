package models.enums;

public enum SortDirection {

  ASC("asc"), DESC("desc");

  private final String text;

  SortDirection(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

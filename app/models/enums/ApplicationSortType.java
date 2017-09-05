package models.enums;

public enum ApplicationSortType {

  DATE("date"), STATUS("status"), CREATED_BY("createdBy");

  private final String text;

  ApplicationSortType(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

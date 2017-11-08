package models.enums;

public enum ApplicationSortType {

  DATE("date"), REFERENCE("reference"), STATUS("status"), CREATED_BY("created-by"), EVENT_TYPE("event-type"), EVENT_DATE("event-date"), DESTINATION("destination");

  private final String text;

  ApplicationSortType(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

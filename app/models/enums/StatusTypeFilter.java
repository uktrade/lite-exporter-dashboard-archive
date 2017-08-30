package models.enums;

public enum StatusTypeFilter {

  ALL("all"), DRAFT("draft"), CURRENT("current"), COMPLETED("completed");

  private final String text;

  StatusTypeFilter(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }


}

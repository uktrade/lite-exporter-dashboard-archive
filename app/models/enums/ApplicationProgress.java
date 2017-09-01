package models.enums;

public enum ApplicationProgress {

  DRAFT("draft"), CURRENT("current"), COMPLETED("completed");

  private final String text;

  ApplicationProgress(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

package models.enums;

public enum Action {

  AMEND("amend"), WITHDRAW("withdraw");

  private final String text;

  Action(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

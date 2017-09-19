package models.enums;

public enum TestType {

  ONE("one"), TWO("two"), OTHER("other"), DEL("del"), DEL_ALL("del-all"), RESET_ALL("reset-all");

  private final String text;

  TestType(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}

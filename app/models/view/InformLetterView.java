package models.view;

public class InformLetterView {

  private final String name;
  private final String link;

  public InformLetterView(String name, String link) {
    this.name = name;
    this.link = link;
  }

  public String getName() {
    return name;
  }

  public String getLink() {
    return link;
  }

}

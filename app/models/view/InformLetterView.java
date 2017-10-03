package models.view;

public class InformLetterView {

  private final String name;
  private final String link;
  private final String anchor;

  public InformLetterView(String name, String link, String anchor) {
    this.name = name;
    this.link = link;
    this.anchor = anchor;
  }

  public String getName() {
    return name;
  }

  public String getLink() {
    return link;
  }

  public String getAnchor() {
    return anchor;
  }

}

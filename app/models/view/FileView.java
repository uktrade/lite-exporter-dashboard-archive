package models.view;


public class FileView {

  private final String name;
  private final String link;
  private final String size;
  private final String jsDeleteLink;
  private final String nonJsDeleteLink;

  public FileView(String name, String link, String size, String jsDeleteLink, String nonJsDeleteLink) {
    this.name = name;
    this.link = link;
    this.size = size;
    this.jsDeleteLink = jsDeleteLink;
    this.nonJsDeleteLink = nonJsDeleteLink;
  }

  public String getName() {
    return name;
  }

  public String getLink() {
    return link;
  }

  public String getSize() {
    return size;
  }

  public String getJsDeleteLink() {
    return jsDeleteLink;
  }

  public String getNonJsDeleteLink() {
    return nonJsDeleteLink;
  }

}

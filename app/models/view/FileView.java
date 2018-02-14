package models.view;


public class FileView {

  private final String id;
  private final String name;
  private final String link;
  private final String size;
  private final String deleteLink;

  public FileView(String id, String name, String link, String size, String deleteLink) {
    this.id = id;
    this.name = name;
    this.link = link;
    this.size = size;
    this.deleteLink = deleteLink;
  }

  public String getId() {
    return id;
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

  public String getDeleteLink() {
    return deleteLink;
  }

}

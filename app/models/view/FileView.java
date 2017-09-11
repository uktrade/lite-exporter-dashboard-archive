package models.view;


public class FileView {

  private String id;
  private String name;
  private String link;
  private String size;

  public FileView(String id, String name, String link, String size) {
    this.id = id;
    this.name = name;
    this.link = link;
    this.size = size;
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

}

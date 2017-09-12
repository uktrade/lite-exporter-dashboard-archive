package models.view;


public class FileView {

  private String id;
  private String relatedId;
  private String name;
  private String link;
  private String deleteLink;
  private String size;

  public FileView(String id, String relatedId, String name, String link, String deleteLink, String size) {
    this.id = id;
    this.relatedId = relatedId;
    this.name = name;
    this.link = link;
    this.deleteLink = deleteLink;
    this.size = size;
  }

  public String getId() {
    return id;
  }

  public String getRelatedId() {
    return relatedId;
  }

  public String getName() {
    return name;
  }

  public String getLink() {
    return link;
  }

  public String getDeleteLink() {
    return deleteLink;
  }

  public String getSize() {
    return size;
  }
}

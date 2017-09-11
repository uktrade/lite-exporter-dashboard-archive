package models;

public class FileUploadResponseItem {

  private final String name;
  private final String url;
  private final String error;
  private final String size;
  private final String fileid;
  private final String filetype;

  public FileUploadResponseItem(String name, String url, String error, String size, String fileid, String filetype) {
    this.name = name;
    this.url = url;
    this.error = error;
    this.size = size;
    this.fileid = fileid;
    this.filetype = filetype;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getError() {
    return error;
  }

  public String getSize() {
    return size;
  }

  public String getFileid() {
    return fileid;
  }

  public String getFiletype() {
    return filetype;
  }

}

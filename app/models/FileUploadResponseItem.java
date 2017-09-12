package models;

public class FileUploadResponseItem {

  private final String name;
  private final String url;
  private final String error;
  private final String size;
  private final String relatedId;
  private final String fileId;
  private final String fileType;

  public FileUploadResponseItem(String name, String url, String error, String size, String relatedId, String fileId, String fileType) {
    this.name = name;
    this.url = url;
    this.error = error;
    this.size = size;
    this.relatedId = relatedId;
    this.fileId = fileId;
    this.fileType = fileType;
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

  public String getRelatedId() {
    return relatedId;
  }

  public String getFileId() {
    return fileId;
  }

  public String getFileType() {
    return fileType;
  }

}

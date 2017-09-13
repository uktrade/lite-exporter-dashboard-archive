package models;

import java.util.List;

public class FileUploadResponse {

  private final List<FileUploadResponseItem> files;

  public FileUploadResponse(List<FileUploadResponseItem> files) {
    this.files = files;
  }

  public List<FileUploadResponseItem> getFiles() {
    return files;
  }

}

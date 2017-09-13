package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class File {

  private String fileId;
  private String name;
  private String path;
  private long createdAt;

  public File(@JsonProperty("fileId") String fileId,
              @JsonProperty("name") String name,
              @JsonProperty("path") String path,
              @JsonProperty("createdAt") long createdAt) {
    this.fileId = fileId;
    this.name = name;
    this.path = path;
    this.createdAt = createdAt;
  }

  public String getFileId() {
    return fileId;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public long getCreatedAt() {
    return createdAt;
  }

}

package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attachment {

  private final String id;
  private final String filename;
  private final String bucket;
  private final String folder;
  private final Long size;

  public Attachment(@JsonProperty("id") String id,
                    @JsonProperty("filename") String filename,
                    @JsonProperty("bucket") String bucket,
                    @JsonProperty("folder") String folder,
                    @JsonProperty("size") Long size) {
    this.id = id;
    this.filename = filename;
    this.bucket = bucket;
    this.folder = folder;
    this.size = size;

  }

  public String getId() {
    return id;
  }

  public String getFilename() {
    return filename;
  }

  public String getBucket() {
    return bucket;
  }

  public String getFolder() {
    return folder;
  }

  public Long getSize() {
    return size;
  }

}

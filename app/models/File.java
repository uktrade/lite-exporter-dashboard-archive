package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

public class File {

  @NotBlank
  private String id;

  @NotBlank
  private String filename;

  @NotBlank
  private String url;

  public File(@JsonProperty("id") String id,
              @JsonProperty("filename") String filename,
              @JsonProperty("url") String url) {
    this.id = id;
    this.filename = filename;
    this.url = url;
  }

  public String getId() {
    return id;
  }

  public String getFilename() {
    return filename;
  }

  public String getUrl() {
    return url;
  }

}

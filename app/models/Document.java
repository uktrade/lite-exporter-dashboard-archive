package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

public class Document {

  @NotBlank
  private final String id;

  @NotBlank
  private final String filename;

  @NotBlank
  private final String url;

  public Document(@JsonProperty("id") String id,
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
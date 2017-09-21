package uk.gov.bis.lite.exporterdashboard.api;

import org.hibernate.validator.constraints.NotBlank;

public class File {

  @NotBlank
  private String id;

  @NotBlank
  private String filename;

  @NotBlank
  private String url;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}

package uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard;

import org.hibernate.validator.constraints.NotEmpty;

public class DashboardMessageDocument {

  @NotEmpty
  private String id;

  @NotEmpty
  private String filename;

  @NotEmpty
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

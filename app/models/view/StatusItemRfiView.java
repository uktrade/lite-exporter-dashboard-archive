package models.view;

public class StatusItemRfiView {

  private final String appId;
  private final String rfiId;
  private final String description;

  public StatusItemRfiView(String appId, String rfiId, String description) {
    this.appId = appId;
    this.rfiId = rfiId;
    this.description = description;
  }

  public String getAppId() {
    return appId;
  }

  public String getRfiId() {
    return rfiId;
  }

  public String getDescription() {
    return description;
  }
}

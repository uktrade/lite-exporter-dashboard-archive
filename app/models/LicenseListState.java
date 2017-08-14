package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LicenseListState {

  private final String tab;
  private final String reference;
  private final String licensee;
  private final String site;
  private final String date;
  private final Integer page;

  @JsonCreator
  public LicenseListState(@JsonProperty("tab") String tab,
                          @JsonProperty("reference") String reference,
                          @JsonProperty("licensee") String licensee,
                          @JsonProperty("site") String site,
                          @JsonProperty("date") String date,
                          @JsonProperty("page") Integer page) {
    this.tab = tab;
    this.reference = reference;
    this.licensee = licensee;
    this.site = site;
    this.date = date;
    this.page = page;
  }

  public String getTab() {
    return tab;
  }

  public String getReference() {
    return reference;
  }

  public String getLicensee() {
    return licensee;
  }

  public String getSite() {
    return site;
  }

  public String getDate() {
    return date;
  }

  public Integer getPage() {
    return page;
  }
}

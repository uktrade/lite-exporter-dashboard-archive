package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationListState {

  private final String tab;
  private final String date;
  private final String status;
  private final String show;
  private final String company;
  private final String createdBy;
  private final Integer page;

  @JsonCreator
  public ApplicationListState(@JsonProperty("tab") String tab,
                              @JsonProperty("date") String date,
                              @JsonProperty("status") String status,
                              @JsonProperty("show") String show,
                              @JsonProperty("company") String company,
                              @JsonProperty("createdBy") String createdBy,
                              @JsonProperty("page") Integer page) {
    this.tab = tab;
    this.date = date;
    this.status = status;
    this.show = show;
    this.company = company;
    this.createdBy = createdBy;
    this.page = page;
  }

  public String getTab() {
    return tab;
  }

  public String getDate() {
    return date;
  }

  public String getStatus() {
    return status;
  }

  public String getShow() {
    return show;
  }

  public String getCompany() {
    return company;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public Integer getPage() {
    return page;
  }
}

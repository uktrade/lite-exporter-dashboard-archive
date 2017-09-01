package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationListState {

  private final String tab;
  private final String sort;
  private final String direction;
  private final String company;
  private final String show;
  private final Integer page;

  @JsonCreator
  public ApplicationListState(@JsonProperty("tab") String tab,
                              @JsonProperty("sort") String sort,
                              @JsonProperty("direction") String direction,
                              @JsonProperty("company") String company,
                              @JsonProperty("show") String show,
                              @JsonProperty("page") Integer page) {
    this.tab = tab;
    this.sort = sort;
    this.direction = direction;
    this.company = company;
    this.show = show;
    this.page = page;
  }

  public String getTab() {
    return tab;
  }

  public String getSort() {
    return sort;
  }

  public String getDirection() {
    return direction;
  }

  public String getCompany() {
    return company;
  }

  public String getShow() {
    return show;
  }

  public Integer getPage() {
    return page;
  }
}

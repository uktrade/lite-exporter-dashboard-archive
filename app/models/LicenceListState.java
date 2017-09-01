package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LicenceListState {

  private final String tab;
  private final String sort;
  private final String direction;
  private final Integer page;

  @JsonCreator
  public LicenceListState(@JsonProperty("tab") String tab,
                          @JsonProperty("sort") String sort,
                          @JsonProperty("direction") String direction,
                          @JsonProperty("page") Integer page) {
    this.tab = tab;
    this.sort = sort;
    this.direction = direction;
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

  public Integer getPage() {
    return page;
  }

}

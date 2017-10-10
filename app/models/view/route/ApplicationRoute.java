package models.view.route;

import models.enums.ApplicationListTab;
import models.enums.ApplicationProgress;
import models.enums.ApplicationSortType;
import models.enums.SortDirection;

public class ApplicationRoute implements Route {

  private ApplicationListTab applicationListTab;
  private String companyId;
  private ApplicationSortType applicationSortType;
  private SortDirection sortDirection;
  private ApplicationProgress applicationProgress;
  private Integer page;

  public ApplicationRoute(ApplicationListTab applicationListTab,
                          String companyId,
                          ApplicationSortType applicationSortType,
                          SortDirection sortDirection,
                          ApplicationProgress applicationProgress,
                          Integer page) {
    this.applicationListTab = applicationListTab;
    this.companyId = companyId;
    this.applicationSortType = applicationSortType;
    this.sortDirection = sortDirection;
    this.applicationProgress = applicationProgress;
    this.page = page;
  }

  public ApplicationRoute nextSort(ApplicationSortType sortType) {
    if (applicationSortType == sortType) {
      nextSortDirection();
    } else {
      applicationSortType = sortType;
      if (applicationSortType == ApplicationSortType.CREATED_BY || applicationSortType == ApplicationSortType.STATUS) {
        sortDirection = SortDirection.ASC;
      } else {
        sortDirection = SortDirection.DESC;
      }
    }
    return this;
  }

  private void nextSortDirection() {
    if (sortDirection == SortDirection.DESC) {
      sortDirection = SortDirection.ASC;
    } else {
      sortDirection = SortDirection.DESC;
    }
  }

  public ApplicationRoute setApplicationListTab(ApplicationListTab applicationListTab) {
    this.applicationListTab = applicationListTab;
    return this;
  }

  public ApplicationRoute setCompanyId(String companyId) {
    this.companyId = companyId;
    return this;
  }

  public ApplicationRoute setApplicationProgress(ApplicationProgress applicationProgress) {
    this.applicationProgress = applicationProgress;
    return this;
  }

  public ApplicationRoute setPage(int page) {
    this.page = page;
    return this;
  }

  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    if (applicationListTab != null) {
      stringBuilder.append("&tab=");
      stringBuilder.append(applicationListTab);
    }
    if (companyId != null) {
      stringBuilder.append("&company=");
      stringBuilder.append(companyId);
    }
    if (applicationSortType != null) {
      stringBuilder.append("&sort=");
      stringBuilder.append(applicationSortType);
    }
    if (sortDirection != null) {
      stringBuilder.append("&direction=");
      stringBuilder.append(sortDirection);
    }
    stringBuilder.append("&show=");
    if (applicationProgress != null) {
      stringBuilder.append(applicationProgress);
    } else {
      stringBuilder.append("all");
    }
    if (page != null && page != 1) {
      stringBuilder.append("&page=");
      stringBuilder.append(page);
    }
    String url = stringBuilder.toString();
    if (url.startsWith("&")) {
      url = "?" + url.substring(1, url.length());
    }
    return "/applications" + url;
  }

}

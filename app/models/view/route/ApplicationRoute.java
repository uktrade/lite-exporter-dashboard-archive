package models.view.route;

import models.enums.ApplicationListTab;
import models.enums.SortDirection;
import models.enums.StatusTypeFilter;

public class ApplicationRoute {

  private ApplicationListTab applicationListTab;
  private String companyId;
  private SortDirection date;
  private SortDirection status;
  private SortDirection createdBy;
  private StatusTypeFilter statusTypeFilter;
  private Integer page;

  public ApplicationRoute(ApplicationListTab applicationListTab, String companyId, SortDirection date, SortDirection status, SortDirection createdBy, StatusTypeFilter statusTypeFilter, Integer page) {
    this.applicationListTab = applicationListTab;
    this.companyId = companyId;
    this.date = date;
    this.status = status;
    this.createdBy = createdBy;
    this.statusTypeFilter = statusTypeFilter;
    this.page = page;
  }

  private SortDirection next(SortDirection sortDirection) {
    if (sortDirection == SortDirection.DESC) {
      return SortDirection.ASC;
    } else {
      return SortDirection.DESC;
    }
  }

  private void clearSortDirections() {
    date = null;
    status = null;
    createdBy = null;
  }

  public ApplicationRoute setApplicationListTab(ApplicationListTab applicationListTab) {
    this.applicationListTab = applicationListTab;
    return this;
  }

  public ApplicationRoute setCompanyId(String companyId) {
    this.companyId = companyId;
    return this;
  }

  public ApplicationRoute nextDate() {
    SortDirection next = next(date);
    clearSortDirections();
    this.date = next;
    return this;
  }

  public ApplicationRoute nextStatus() {
    SortDirection next = next(status);
    clearSortDirections();
    this.status = next;
    return this;
  }

  public ApplicationRoute nextCreatedBy() {
    SortDirection next = next(createdBy);
    clearSortDirections();
    this.createdBy = next;
    return this;
  }

  public ApplicationRoute setStatusTypeFilter(StatusTypeFilter statusTypeFilter) {
    this.statusTypeFilter = statusTypeFilter;
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
    if (date != null) {
      stringBuilder.append("&date=");
      stringBuilder.append(date);
    }
    if (status != null) {
      stringBuilder.append("&status=");
      stringBuilder.append(status);
    }
    if (createdBy != null && applicationListTab == ApplicationListTab.COMPANY) {
      stringBuilder.append("&createdBy=");
      stringBuilder.append(createdBy);
    }
    if (statusTypeFilter != null) {
      stringBuilder.append("&show=");
      stringBuilder.append(statusTypeFilter);
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

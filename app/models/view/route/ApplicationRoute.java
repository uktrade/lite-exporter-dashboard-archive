package models.view.route;

import models.enums.StatusTypeFilter;

public class ApplicationRoute {

  private String tab;
  private String companyId;
  private String date;
  private String status;
  private StatusTypeFilter statusTypeFilter;
  private String createdBy;
  private Integer page;

  public ApplicationRoute(String tab, String companyId, String date, String status, String createdBy, StatusTypeFilter statusTypeFilter, Integer page) {
    this.tab = tab;
    this.companyId = companyId;
    this.date = date;
    this.status = status;
    this.statusTypeFilter = statusTypeFilter;
    this.createdBy = createdBy;
    this.page = page;
  }

  private String next(String sortDirection) {
    if ("desc".equals(sortDirection)) {
      return "asc";
    } else {
      return "desc";
    }
  }

  private void clearSortDirections() {
    date = null;
    status = null;
    createdBy = null;
  }

  public ApplicationRoute setTab(String tab) {
    this.tab = tab;
    return this;
  }

  public ApplicationRoute setCompanyId(String companyId) {
    this.companyId = companyId;
    return this;
  }

  public ApplicationRoute nextDate() {
    String next = next(date);
    clearSortDirections();
    this.date = next;
    return this;
  }

  public ApplicationRoute nextStatus() {
    String next = next(status);
    clearSortDirections();
    this.status = next;
    return this;
  }

  public ApplicationRoute nextCreatedBy() {
    String next = next(createdBy);
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
    if (tab != null) {
      stringBuilder.append("&tab=" + tab);
    }
    if (companyId != null) {
      stringBuilder.append("&company=" + companyId);
    }
    if (date != null) {
      stringBuilder.append("&date=" + date);
    }
    if (status != null) {
      stringBuilder.append("&status=" + status);
    }
    if (createdBy != null && "created-by-your-company".equals(tab)) {
      stringBuilder.append("&createdBy=" + createdBy);
    }
    if (statusTypeFilter != null) {
      stringBuilder.append("&show=" + statusTypeFilter.toString().toLowerCase());
    }
    if (page != null && page != 1) {
      stringBuilder.append("&page=" + page);
    }
    String url = stringBuilder.toString();
    if (url.startsWith("&")) {
      url = "?" + url.substring(1, url.length());
    }
    return "/applications" + url;
  }

}

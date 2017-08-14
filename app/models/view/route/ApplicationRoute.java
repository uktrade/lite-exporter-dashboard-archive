package models.view.route;

import models.enums.StatusTypeFilter;

public class ApplicationRoute {

  private String tab;
  private String companyId;
  private String date;
  private String status;
  private StatusTypeFilter statusTypeFilter;
  private Integer page;

  public ApplicationRoute(String tab, String companyId, String date, String status, StatusTypeFilter statusTypeFilter, Integer page) {
    this.tab = tab;
    this.companyId = companyId;
    this.date = date;
    this.status = status;
    this.statusTypeFilter = statusTypeFilter;
    this.page = page;
  }

  private String next(String sortDirection) {
    if ("desc".equals(sortDirection)) {
      return "asc";
    } else {
      return "desc";
    }
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
    this.date = next(date);
    this.status = null;
    return this;
  }

  public ApplicationRoute nextStatus() {
    this.status = next(status);
    this.date = null;
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
      stringBuilder.append("&companyId=" + companyId);
    }
    if (date != null) {
      stringBuilder.append("&date=" + date);
    }
    if (status != null) {
      stringBuilder.append("&status=" + status);
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

package models.view;

import models.enums.StatusTypeFilter;

import java.util.List;

public class ApplicationListView {

  private final List<ApplicationItemView> applicationItemViews;
  private final String companyId;
  private final List<CompanySelectItemView> companySelectItemViews;
  private final String dateSortDirection;
  private final String nextDateSortDirection;
  private final String statusSortDirection;
  private final String nextStatusSortDirection;
  private final StatusTypeFilter statusTypeFilter;
  private final long allCount;
  private final long draftCount;
  private final long currentCount;
  private final long completedCount;
  private final int currentPage;
  private final int pageCount;
  private final int showingFrom;
  private final int showingTo;
  private final int applicationCount;

  public ApplicationListView(List<ApplicationItemView> applicationItemViews, String companyId, List<CompanySelectItemView> companySelectItemViews, String dateSortDirection, String nextDateSortDirection, String statusSortDirection, String nextStatusSortDirection, StatusTypeFilter statusTypeFilter, long allCount, long draftCount, long currentCount, long completedCount, int currentPage, int pageCount, int showingFrom, int showingTo, int applicationCount) {
    this.applicationItemViews = applicationItemViews;
    this.companyId = companyId;
    this.companySelectItemViews = companySelectItemViews;
    this.dateSortDirection = dateSortDirection;
    this.nextDateSortDirection = nextDateSortDirection;
    this.statusSortDirection = statusSortDirection;
    this.nextStatusSortDirection = nextStatusSortDirection;
    this.statusTypeFilter = statusTypeFilter;
    this.allCount = allCount;
    this.draftCount = draftCount;
    this.currentCount = currentCount;
    this.completedCount = completedCount;
    this.currentPage = currentPage;
    this.pageCount = pageCount;
    this.showingFrom = showingFrom;
    this.showingTo = showingTo;
    this.applicationCount = applicationCount;
  }

  public List<ApplicationItemView> getApplicationItemViews() {
    return applicationItemViews;
  }

  public String getCompanyId() {
    return companyId;
  }

  public List<CompanySelectItemView> getCompanySelectItemViews() {
    return companySelectItemViews;
  }

  public String getDateSortDirection() {
    return dateSortDirection;
  }

  public String getNextDateSortDirection() {
    return nextDateSortDirection;
  }

  public String getStatusSortDirection() {
    return statusSortDirection;
  }

  public String getNextStatusSortDirection() {
    return nextStatusSortDirection;
  }

  public StatusTypeFilter getStatusTypeFilter() {
    return statusTypeFilter;
  }

  public long getAllCount() {
    return allCount;
  }

  public long getDraftCount() {
    return draftCount;
  }

  public long getCurrentCount() {
    return currentCount;
  }

  public long getCompletedCount() {
    return completedCount;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public int getPageCount() {
    return pageCount;
  }

  public int getShowingFrom() {
    return showingFrom;
  }

  public int getShowingTo() {
    return showingTo;
  }

  public int getApplicationCount() {
    return applicationCount;
  }
}

package models.view;

import models.Page;
import models.enums.StatusTypeFilter;
import models.view.route.ApplicationRoute;

import java.util.List;

public class ApplicationListView {

  private final String tab;
  private final String companyId;
  private final List<CompanySelectItemView> companySelectItemViews;
  private final String dateSortDirection;
  private final String statusSortDirection;
  private final StatusTypeFilter statusTypeFilter;
  private final long allCount;
  private final long draftCount;
  private final long currentCount;
  private final long completedCount;
  private final Page<ApplicationItemView> page;

  public ApplicationListView(String tab, String companyId, List<CompanySelectItemView> companySelectItemViews, String dateSortDirection, String statusSortDirection, StatusTypeFilter statusTypeFilter, long allCount, long draftCount, long currentCount, long completedCount, Page<ApplicationItemView> page) {
    this.tab = tab;
    this.companyId = companyId;
    this.companySelectItemViews = companySelectItemViews;
    this.dateSortDirection = dateSortDirection;
    this.statusSortDirection = statusSortDirection;
    this.statusTypeFilter = statusTypeFilter;
    this.allCount = allCount;
    this.draftCount = draftCount;
    this.currentCount = currentCount;
    this.completedCount = completedCount;
    this.page = page;
  }

  public String getTab() {
    return tab;
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

  public String getStatusSortDirection() {
    return statusSortDirection;
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

  public Page<ApplicationItemView> getPage() {
    return page;
  }

  public ApplicationRoute getApplicationRoute() {
    return new ApplicationRoute(tab, companyId, dateSortDirection, statusSortDirection, statusTypeFilter, page.getCurrentPage());
  }
}

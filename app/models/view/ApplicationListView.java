package models.view;

import models.Page;
import models.enums.ApplicationListTab;
import models.enums.SortDirection;
import models.enums.StatusTypeFilter;
import models.view.route.ApplicationRoute;

import java.util.List;

public class ApplicationListView {

  private final ApplicationListTab applicationListTab;
  private final String companyId;
  private final List<CompanySelectItemView> companySelectItemViews;
  private final boolean showCompanyTab;
  private final SortDirection date;
  private final SortDirection status;
  private final SortDirection createdBy;
  private final StatusTypeFilter statusTypeFilter;
  private final long allCount;
  private final long draftCount;
  private final long currentCount;
  private final long completedCount;
  private final Page<ApplicationItemView> page;

  public ApplicationListView(ApplicationListTab applicationListTab,
                             String companyId,
                             List<CompanySelectItemView> companySelectItemViews,
                             boolean showCompanyTab,
                             SortDirection date,
                             SortDirection status,
                             SortDirection createdBy,
                             StatusTypeFilter statusTypeFilter,
                             long allCount,
                             long draftCount,
                             long currentCount,
                             long completedCount,
                             Page<ApplicationItemView> page) {
    this.applicationListTab = applicationListTab;
    this.companyId = companyId;
    this.companySelectItemViews = companySelectItemViews;
    this.showCompanyTab = showCompanyTab;
    this.date = date;
    this.status = status;
    this.createdBy = createdBy;
    this.statusTypeFilter = statusTypeFilter;
    this.allCount = allCount;
    this.draftCount = draftCount;
    this.currentCount = currentCount;
    this.completedCount = completedCount;
    this.page = page;
  }

  public ApplicationListTab getApplicationListTab() {
    return applicationListTab;
  }

  public String getCompanyId() {
    return companyId;
  }

  public List<CompanySelectItemView> getCompanySelectItemViews() {
    return companySelectItemViews;
  }

  public boolean isShowCompanyTab() {
    return showCompanyTab;
  }

  public SortDirection getDate() {
    return date;
  }

  public SortDirection getStatus() {
    return status;
  }

  public SortDirection getCreatedBy() {
    return createdBy;
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
    return new ApplicationRoute(applicationListTab, companyId, date, status, createdBy, statusTypeFilter, page.getCurrentPage());
  }

}

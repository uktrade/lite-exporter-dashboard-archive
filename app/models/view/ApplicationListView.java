package models.view;

import java.util.List;
import models.Page;
import models.enums.ApplicationListTab;
import models.enums.ApplicationProgress;
import models.enums.ApplicationSortType;
import models.enums.SortDirection;
import models.view.route.ApplicationRoute;

public class ApplicationListView implements ListView {

  private final ApplicationListTab applicationListTab;
  private final String companyId;
  private final List<CompanySelectItemView> companySelectItemViews;
  private final boolean hasUserApplications;
  private final boolean hasOtherUserApplications;
  private final boolean hasForYourAttentionApplications;
  private final ApplicationSortType applicationSortType;
  private final SortDirection sortDirection;
  private final ApplicationProgress applicationProgress;
  private final long allCount;
  private final long draftCount;
  private final long currentCount;
  private final long completedCount;
  private final Page<ApplicationItemView> page;

  public ApplicationListView(ApplicationListTab applicationListTab,
                             String companyId,
                             List<CompanySelectItemView> companySelectItemViews,
                             boolean hasUserApplications,
                             boolean hasOtherUserApplications,
                             boolean hasForYourAttentionApplications,
                             ApplicationSortType applicationSortType,
                             SortDirection sortDirection,
                             ApplicationProgress applicationProgress,
                             long allCount,
                             long draftCount,
                             long currentCount,
                             long completedCount,
                             Page<ApplicationItemView> page) {
    this.applicationListTab = applicationListTab;
    this.companyId = companyId;
    this.companySelectItemViews = companySelectItemViews;
    this.hasUserApplications = hasUserApplications;
    this.hasOtherUserApplications = hasOtherUserApplications;
    this.hasForYourAttentionApplications = hasForYourAttentionApplications;
    this.applicationSortType = applicationSortType;
    this.sortDirection = sortDirection;
    this.applicationProgress = applicationProgress;
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

  public boolean isHasUserApplications() {
    return hasUserApplications;
  }

  public boolean isHasOtherUserApplications() {
    return hasOtherUserApplications;
  }

  public boolean isHasForYourAttentionApplications() {
    return hasForYourAttentionApplications;
  }

  public ApplicationSortType getApplicationSortType() {
    return applicationSortType;
  }

  public SortDirection getSortDirection() {
    return sortDirection;
  }

  public ApplicationProgress getApplicationProgress() {
    return applicationProgress;
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

  public ApplicationRoute getRoute() {
    return new ApplicationRoute(applicationListTab, companyId, applicationSortType, sortDirection, applicationProgress, page.getCurrentPage());
  }

}

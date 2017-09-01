package controllers;

import static components.util.StreamUtil.distinctByKey;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.service.ApplicationItemViewService;
import components.service.CacheService;
import components.service.PageService;
import components.service.UserService;
import components.util.EnumUtil;
import components.util.FilterUtil;
import components.util.SortUtil;
import models.ApplicationListState;
import models.Page;
import models.User;
import models.enums.ApplicationListTab;
import models.enums.ApplicationProgress;
import models.enums.ApplicationSortType;
import models.enums.SortDirection;
import models.view.ApplicationItemView;
import models.view.ApplicationListView;
import models.view.CompanySelectItemView;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.applicationList;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationListController extends Controller {

  private final String licenceApplicationAddress;
  private final ApplicationItemViewService applicationItemViewService;
  private final CacheService cacheService;
  private final PageService pageService;
  private final UserService userService;

  @Inject
  public ApplicationListController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                                   ApplicationItemViewService applicationItemViewService,
                                   CacheService cacheService,
                                   PageService pageService,
                                   UserService userService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.applicationItemViewService = applicationItemViewService;
    this.cacheService = cacheService;
    this.pageService = pageService;
    this.userService = userService;
  }

  public Result index() {
    return redirect("/applications");
  }

  public Result applicationList(String tab, String sort, String direction, String company, String show, Integer page) {
    User currentUser = userService.getCurrentUser();

    ApplicationListState state = cacheService.getApplicationListState(tab, sort, direction, company, show, page);
    ApplicationProgress applicationProgress = EnumUtil.parse(state.getShow(), ApplicationProgress.class);
    ApplicationSortType applicationSortType = EnumUtil.parse(state.getSort(), ApplicationSortType.class, ApplicationSortType.DATE);
    SortDirection sortDirection = EnumUtil.parse(state.getDirection(), SortDirection.class, SortDirection.DESC);
    ApplicationListTab applicationListTab = EnumUtil.parse(state.getTab(), ApplicationListTab.class, ApplicationListTab.USER);

    if (applicationSortType == ApplicationSortType.CREATED_BY && applicationListTab != ApplicationListTab.COMPANY) {
      applicationSortType = ApplicationSortType.DATE;
    }

    List<ApplicationItemView> views = applicationItemViewService.getApplicationItemViews(currentUser.getId());

    long otherUserCount = views.stream()
        .map(ApplicationItemView::getCreatedById)
        .filter(id -> !currentUser.getId().equals(id))
        .distinct()
        .count();
    boolean showCompanyTab = otherUserCount > 0;

    List<ApplicationItemView> userFilteredViews = FilterUtil.filterByUser(currentUser.getId(), applicationListTab, views);

    List<CompanySelectItemView> companyNames = views.stream()
        .filter(distinctByKey(ApplicationItemView::getCompanyId))
        .map(view -> new CompanySelectItemView(view.getCompanyId(), view.getCompanyName()))
        .sorted(Comparator.comparing(CompanySelectItemView::getCompanyName))
        .collect(Collectors.toList());

    String companyId = state.getCompany();
    List<ApplicationItemView> companyFilteredViews = FilterUtil.filterByCompanyId(companyId, userFilteredViews);

    long allCount = companyFilteredViews.size();
    long draftCount = count(companyFilteredViews, ApplicationProgress.DRAFT);
    long completedCount = count(companyFilteredViews, ApplicationProgress.COMPLETED);
    long currentCount = allCount - draftCount - completedCount;

    List<ApplicationItemView> statusTypeFilteredViews = FilterUtil.filterByApplicationProgress(applicationProgress, companyFilteredViews);

    SortUtil.sort(statusTypeFilteredViews, applicationSortType, sortDirection);

    Page<ApplicationItemView> pageData = pageService.getPage(state.getPage(), statusTypeFilteredViews);

    ApplicationListView applicationListView = new ApplicationListView(applicationListTab,
        companyId,
        companyNames,
        showCompanyTab,
        applicationSortType,
        sortDirection,
        applicationProgress,
        allCount,
        draftCount,
        currentCount,
        completedCount,
        pageData);

    return ok(applicationList.render(licenceApplicationAddress, applicationListView));
  }

  private long count(List<ApplicationItemView> applicationItemViews, ApplicationProgress applicationProgress) {
    return applicationItemViews.stream()
        .filter(view -> view.getApplicationProgress() == applicationProgress)
        .count();
  }

}

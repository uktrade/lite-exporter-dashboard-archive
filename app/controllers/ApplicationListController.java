package controllers;

import static components.util.StreamUtil.distinctByKey;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.cache.SessionCache;
import components.service.ApplicationItemViewService;
import components.service.UserService;
import components.util.EnumUtil;
import components.util.PageUtil;
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
  private final UserService userService;

  @Inject
  public ApplicationListController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                                   ApplicationItemViewService applicationItemViewService,
                                   UserService userService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.applicationItemViewService = applicationItemViewService;
    this.userService = userService;
  }

  public Result index() {
    return redirect("/applications");
  }

  public Result applicationList(String tab, String sort, String direction, String company, String show, Integer page) {
    User currentUser = userService.getCurrentUser();

    ApplicationListState state = SessionCache.getApplicationListState(tab, sort, direction, company, show, page);
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

    List<ApplicationItemView> userFilteredViews = filterByUser(currentUser.getId(), applicationListTab, views);

    List<CompanySelectItemView> companyNames = views.stream()
        .filter(distinctByKey(ApplicationItemView::getCompanyId))
        .map(view -> new CompanySelectItemView(view.getCompanyId(), view.getCompanyName()))
        .sorted(Comparator.comparing(CompanySelectItemView::getCompanyName))
        .collect(Collectors.toList());

    String companyId = state.getCompany();
    List<ApplicationItemView> companyFilteredViews = filterByCompanyId(companyId, userFilteredViews);

    long allCount = companyFilteredViews.size();
    long draftCount = count(companyFilteredViews, ApplicationProgress.DRAFT);
    long completedCount = count(companyFilteredViews, ApplicationProgress.COMPLETED);
    long currentCount = allCount - draftCount - completedCount;

    List<ApplicationItemView> statusTypeFilteredViews = filterByApplicationProgress(applicationProgress, companyFilteredViews);

    SortUtil.sort(statusTypeFilteredViews, applicationSortType, sortDirection);

    Page<ApplicationItemView> pageData = PageUtil.getPage(state.getPage(), statusTypeFilteredViews);

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

  private List<ApplicationItemView> filterByUser(String userId, ApplicationListTab applicationListTab, List<ApplicationItemView> applicationItemViews) {
    if (applicationListTab == ApplicationListTab.USER) {
      return applicationItemViews.stream()
          .filter(view -> userId.equals(view.getCreatedById()))
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

  private List<ApplicationItemView> filterByCompanyId(String companyId, List<ApplicationItemView> applicationItemViews) {
    if (companyId != null && !companyId.equals("all")) {
      return applicationItemViews.stream()
          .filter(view -> companyId.equals(view.getCompanyId()))
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

  private List<ApplicationItemView> filterByApplicationProgress(ApplicationProgress applicationProgress, List<ApplicationItemView> applicationItemViews) {
    if (applicationProgress != null) {
      return applicationItemViews.stream()
          .filter(view -> view.getApplicationProgress() == applicationProgress)
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

}

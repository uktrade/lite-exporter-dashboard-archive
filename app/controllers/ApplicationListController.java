package controllers;

import static components.util.StreamUtil.distinctByKey;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.service.ApplicationFilterService;
import components.service.ApplicationItemViewService;
import components.service.ApplicationSortService;
import components.service.CacheService;
import components.service.PageService;
import components.service.UserService;
import components.util.EnumUtil;
import models.ApplicationListState;
import models.Page;
import models.User;
import models.enums.ApplicationListTab;
import models.enums.SortDirection;
import models.enums.StatusType;
import models.enums.StatusTypeFilter;
import models.view.ApplicationItemView;
import models.view.ApplicationListView;
import models.view.CompanySelectItemView;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import views.html.applicationList;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationListController extends Controller {

  private final String licenceApplicationAddress;
  private final ApplicationItemViewService applicationItemViewService;
  private final CacheService cacheService;
  private final PageService pageService;
  private final UserService userService;
  private final ApplicationFilterService applicationFilterService;
  private final ApplicationSortService applicationSortService;

  @Inject
  public ApplicationListController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                                   ApplicationItemViewService applicationItemViewService,
                                   CacheService cacheService,
                                   PageService pageService,
                                   UserService userService,
                                   ApplicationFilterService applicationFilterService,
                                   ApplicationSortService applicationSortService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.applicationItemViewService = applicationItemViewService;
    this.cacheService = cacheService;
    this.pageService = pageService;
    this.userService = userService;
    this.applicationFilterService = applicationFilterService;
    this.applicationSortService = applicationSortService;
  }

  public Result index() {
    return redirect("/applications");
  }

  public Result applicationList(Option<String> tab, Option<String> date, Option<String> status, Option<String> show, Option<String> company, Option<String> createdBy, Option<Integer> page) {
    User currentUser = userService.getCurrentUser();

    ApplicationListState state = cacheService.getApplicationListState(tab, date, status, show, company, createdBy, page);

    StatusTypeFilter statusTypeFilter = EnumUtil.parse(state.getShow(), StatusTypeFilter.class, StatusTypeFilter.ALL);

    SortDirection dateSortDirection = EnumUtil.parse(state.getDate(), SortDirection.class);
    SortDirection statusSortDirection = EnumUtil.parse(state.getStatus(), SortDirection.class);
    SortDirection createdBySortDirection = EnumUtil.parse(state.getCreatedBy(), SortDirection.class);

    ApplicationListTab applicationListTab = EnumUtil.parse(state.getTab(), ApplicationListTab.class, ApplicationListTab.USER);

    if (applicationListTab != ApplicationListTab.COMPANY) {
      createdBySortDirection = null;
    }

    long definedCount = Stream.of(dateSortDirection, statusSortDirection, createdBySortDirection).filter(Objects::nonNull).count();
    if (definedCount != 1) {
      dateSortDirection = SortDirection.DESC;
      statusSortDirection = null;
      createdBySortDirection = null;
    }

    List<ApplicationItemView> views = applicationItemViewService.getApplicationItemViews(currentUser.getId());

    applicationSortService.sort(views, dateSortDirection, statusSortDirection, createdBySortDirection);

    long otherUserCount = views.stream()
        .map(ApplicationItemView::getCreatedById)
        .filter(id -> !currentUser.getId().equals(id))
        .distinct()
        .count();
    boolean showCompanyTab = otherUserCount > 0;

    List<ApplicationItemView> userFilteredViews = applicationFilterService.filterByUser(currentUser.getId(), applicationListTab, views);

    List<CompanySelectItemView> companyNames = views.stream()
        .filter(distinctByKey(ApplicationItemView::getCompanyId))
        .map(view -> new CompanySelectItemView(view.getCompanyId(), view.getCompanyName()))
        .sorted(Comparator.comparing(CompanySelectItemView::getCompanyName))
        .collect(Collectors.toList());

    String companyId = state.getCompany();
    List<ApplicationItemView> companyFilteredViews = applicationFilterService.filterByCompanyId(companyId, userFilteredViews);

    long allCount = companyFilteredViews.size();
    long draftCount = companyFilteredViews.stream()
        .filter(view -> view.getSubmittedTimestamp() == null)
        .count();
    long completedCount = companyFilteredViews.stream()
        .filter(view -> view.getStatusType() == StatusType.COMPLETE)
        .count();
    long currentCount = allCount - draftCount - completedCount;

    List<ApplicationItemView> statusTypeFilteredViews = applicationFilterService.filterByStatusType(statusTypeFilter, companyFilteredViews);

    Page<ApplicationItemView> pageData = pageService.getPage(state.getPage(), statusTypeFilteredViews);

    ApplicationListView applicationListView = new ApplicationListView(applicationListTab,
        companyId,
        companyNames,
        showCompanyTab,
        dateSortDirection,
        statusSortDirection,
        createdBySortDirection,
        statusTypeFilter,
        allCount,
        draftCount,
        currentCount,
        completedCount,
        pageData);

    return ok(applicationList.render(licenceApplicationAddress, applicationListView));
  }

}

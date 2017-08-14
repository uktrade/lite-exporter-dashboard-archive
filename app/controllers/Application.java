package controllers;

import static components.util.StreamUtil.distinctByKey;

import com.google.inject.Inject;
import components.service.ApplicationItemViewService;
import components.service.CacheService;
import components.service.PageService;
import components.service.SortDirectionService;
import components.util.EnumUtil;
import models.ApplicationListState;
import models.Page;
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
import views.html.licenceDetails;

import java.util.List;
import java.util.stream.Collectors;

public class Application extends Controller {

  private final ApplicationItemViewService applicationItemViewService;
  private final CacheService cacheService;
  private final SortDirectionService sortDirectionService;
  private final PageService pageService;

  @Inject
  public Application(ApplicationItemViewService applicationItemViewService, CacheService cacheService, SortDirectionService sortDirectionService, PageService pageService) {
    this.applicationItemViewService = applicationItemViewService;
    this.cacheService = cacheService;
    this.sortDirectionService = sortDirectionService;
    this.pageService = pageService;
  }

  public Result index() {
    return redirect("/applications");
  }

  public Result applicationList(Option<String> tab, Option<String> date, Option<String> status, Option<String> show, Option<String> company, Option<Integer> page) {

    ApplicationListState state = cacheService.getApplicationListState(tab, date, status, show, company, page);

    StatusTypeFilter statusTypeFilter = EnumUtil.parse(StatusTypeFilter.class, state.getShow(), StatusTypeFilter.ALL);

    SortDirection dateSortDirection = sortDirectionService.parse(state.getDate());
    SortDirection statusSortDirection = sortDirectionService.parse(state.getStatus());

    long definedCount = sortDirectionService.definedCount(dateSortDirection, statusSortDirection);
    if (definedCount != 1) {
      dateSortDirection = SortDirection.DESC;
      statusSortDirection = null;
    }

    List<ApplicationItemView> applicationItemViews = applicationItemViewService.getApplicationItemViews(dateSortDirection, statusSortDirection);

    List<CompanySelectItemView> companyNames = applicationItemViews.stream().
        filter(distinctByKey(ApplicationItemView::getCompanyId))
        .map(applicationItemView -> new CompanySelectItemView(applicationItemView.getCompanyId(), applicationItemView.getCompanyName()))
        .collect(Collectors.toList());

    String companyId = state.getCompany();
    if (companyId != null) {
      applicationItemViews = applicationItemViews.stream().filter(view -> companyId.equals(view.getCompanyId())).collect(Collectors.toList());
    }

    String activeTab = "created-by-your-company".equals(state.getTab()) ? "created-by-your-company" : "created-by-you";

    long allCount = applicationItemViews.size();
    long draftCount = applicationItemViews.stream().filter(view -> view.getStatusType() == StatusType.DRAFT).count();
    long completedCount = applicationItemViews.stream().filter(view -> view.getStatusType() == StatusType.COMPLETE).count();
    long currentCount = allCount - draftCount - completedCount;

    List<ApplicationItemView> filteredApplicationViews;
    if (statusTypeFilter == StatusTypeFilter.DRAFT) {
      filteredApplicationViews = applicationItemViews.stream().filter(view -> view.getStatusType() == StatusType.DRAFT).collect(Collectors.toList());
    } else if (statusTypeFilter == StatusTypeFilter.COMPLETED) {
      filteredApplicationViews = applicationItemViews.stream().filter(view -> view.getStatusType() == StatusType.COMPLETE).collect(Collectors.toList());
    } else if (statusTypeFilter == StatusTypeFilter.CURRENT) {
      filteredApplicationViews = applicationItemViews.stream().filter(view -> view.getStatusType() != StatusType.DRAFT && view.getStatusType() != StatusType.COMPLETE).collect(Collectors.toList());
    } else {
      filteredApplicationViews = applicationItemViews;
    }

    Page<ApplicationItemView> pageData = pageService.getPage(state.getPage(), filteredApplicationViews);

    ApplicationListView applicationListView = new ApplicationListView(pageData.getItems(),
        companyId,
        companyNames,
        sortDirectionService.toParam(dateSortDirection),
        sortDirectionService.toNextParam(dateSortDirection),
        sortDirectionService.toParam(statusSortDirection),
        sortDirectionService.toNextParam(statusSortDirection),
        statusTypeFilter,
        allCount,
        draftCount,
        currentCount,
        completedCount,
        pageData.getCurrentPage(),
        pageData.getPageCount(),
        pageData.getFrom(),
        pageData.getTo(),
        filteredApplicationViews.size());
    return ok(applicationList.render(activeTab, applicationListView));
  }

  public Result licenceDetails(String licenceRef) {
    return ok(licenceDetails.render(licenceRef));
  }

}

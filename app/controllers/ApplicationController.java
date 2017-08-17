package controllers;

import static components.util.StreamUtil.distinctByKey;

import com.google.inject.Inject;
import com.google.inject.name.Named;
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
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import views.html.applicationList;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationController extends Controller {

  private final ApplicationItemViewService applicationItemViewService;
  private final CacheService cacheService;
  private final SortDirectionService sortDirectionService;
  private final PageService pageService;
  private final String licenceApplicationAddress;
  private final FormFactory formFactory;

  @Inject
  public ApplicationController(ApplicationItemViewService applicationItemViewService,
                               CacheService cacheService,
                               SortDirectionService sortDirectionService,
                               PageService pageService,
                               @Named("licenceApplicationAddress") String licenceApplicationAddress,
                               FormFactory formFactory) {
    this.applicationItemViewService = applicationItemViewService;
    this.cacheService = cacheService;
    this.sortDirectionService = sortDirectionService;
    this.pageService = pageService;
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.formFactory = formFactory;
  }

  public Result index() {
    return redirect("/applications");
  }

  public Result applicationList(Option<String> tab, Option<String> date, Option<String> status, Option<String> show, Option<String> company, Option<String> createdBy, Option<Integer> page) {

    ApplicationListState state = cacheService.getApplicationListState(tab, date, status, show, company, createdBy, page);

    StatusTypeFilter statusTypeFilter = EnumUtil.parse(StatusTypeFilter.class, state.getShow(), StatusTypeFilter.ALL);

    SortDirection dateSortDirection = sortDirectionService.parse(state.getDate());
    SortDirection statusSortDirection = sortDirectionService.parse(state.getStatus());
    SortDirection createdBySortDirection = sortDirectionService.parse(state.getCreatedBy());

    String activeTab = "created-by-your-company".equals(state.getTab()) ? "created-by-your-company" : "created-by-you";

    if (!"created-by-your-company".equals(activeTab) && createdBySortDirection != null) {
      createdBySortDirection = null;
    }
    long definedCount = sortDirectionService.definedCount(dateSortDirection, statusSortDirection, createdBySortDirection);
    if (definedCount != 1) {
      dateSortDirection = SortDirection.DESC;
      statusSortDirection = null;
    }

    List<ApplicationItemView> applicationItemViews = applicationItemViewService.getApplicationItemViews(dateSortDirection, statusSortDirection, createdBySortDirection);

    List<CompanySelectItemView> companyNames = applicationItemViews.stream().
        filter(distinctByKey(ApplicationItemView::getCompanyId))
        .map(applicationItemView -> new CompanySelectItemView(applicationItemView.getCompanyId(), applicationItemView.getCompanyName()))
        .sorted(Comparator.comparing(CompanySelectItemView::getCompanyName))
        .collect(Collectors.toList());

    String companyId = state.getCompany();
    if (companyId != null && !companyId.equals("all")) {
      applicationItemViews = applicationItemViews.stream().filter(view -> companyId.equals(view.getCompanyId())).collect(Collectors.toList());
    }


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

    ApplicationListView applicationListView = new ApplicationListView(activeTab,
        companyId,
        companyNames,
        sortDirectionService.toParam(dateSortDirection),
        sortDirectionService.toParam(statusSortDirection),
        sortDirectionService.toParam(createdBySortDirection),
        statusTypeFilter,
        allCount,
        draftCount,
        currentCount,
        completedCount,
        pageData);

    return ok(applicationList.render(licenceApplicationAddress, applicationListView));
  }

}

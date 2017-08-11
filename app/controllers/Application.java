package controllers;

import static components.util.StreamUtil.distinctByKey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import components.service.ApplicationItemViewService;
import components.util.EnumUtil;
import models.ApplicationListState;
import models.enums.SortDirection;
import models.enums.StatusType;
import models.enums.StatusTypeFilter;
import models.view.ApplicationItemView;
import models.view.ApplicationListView;
import models.view.CompanySelectItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import views.html.applicationList;
import views.html.licenceDetails;
import views.html.licenceList;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Application extends Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private final ApplicationItemViewService applicationItemViewService;

  @Inject
  public Application(ApplicationItemViewService applicationItemViewService) {
    this.applicationItemViewService = applicationItemViewService;
  }

  public Result index() {
    return redirect("/applications");
  }

  public Result applicationList(Option<String> tab, Option<String> date, Option<String> status, Option<String> show, Option<String> company, Option<Integer> page) throws JsonProcessingException {

    ApplicationListState state = null;
    if (tab.isEmpty() && date.isEmpty() && status.isEmpty() && show.isEmpty() && company.isEmpty() && page.isEmpty()) {
      String applicationListStateJson = session("applicationListState");
      if (applicationListStateJson != null) {
        try {
          state = MAPPER.readValue(applicationListStateJson, ApplicationListState.class);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    if (state == null) {
      state = new ApplicationListState(parse(tab), parse(date), parse(status), parse(show), parse(company), parseNumber(page));
      session("applicationListState", MAPPER.writeValueAsString(state));
    }

    return applicationList(state);
  }

  private String parse(Option<String> str) {
    return str.isDefined() ? str.get() : null;
  }

  private Integer parseNumber(Option<Integer> number) {
    return number.isDefined() ? number.get() : null;
  }

  private Result applicationList(ApplicationListState state) {
    StatusTypeFilter statusTypeFilter = EnumUtil.parse(StatusTypeFilter.class, state.getShow(), StatusTypeFilter.ALL);

    SortDirection dateSortDirection = EnumUtil.parse(SortDirection.class, state.getDate(), null);
    SortDirection statusSortDirection = EnumUtil.parse(SortDirection.class, state.getStatus(), null);
    // If both sort directions were defined, default to date sort direction
    if (dateSortDirection != null && statusSortDirection != null) {
      statusSortDirection = null;
    }
    // If no sort directions were defined, default to date descending
    else if (dateSortDirection == null && statusSortDirection == null) {
      dateSortDirection = SortDirection.DESC;
    }

    SortDirection nextDateSortDirection = SortDirection.DESC;
    if (dateSortDirection == SortDirection.DESC) {
      nextDateSortDirection = SortDirection.ASC;
    }

    SortDirection nextStatusSortDirection = SortDirection.DESC;
    if (statusSortDirection == SortDirection.DESC) {
      nextStatusSortDirection = SortDirection.ASC;
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

    int currentPage = state.getPage() == null ? 1 : state.getPage();
    currentPage = Math.min(currentPage, (filteredApplicationViews.size() / 10) + 1);

    // return only views according to page;
    int start = (currentPage - 1) * 10;
    int max = Math.min(currentPage * 10, filteredApplicationViews.size());
    List<ApplicationItemView> sublist = filteredApplicationViews.subList(start, max);

    ApplicationListView applicationListView = new ApplicationListView(sublist,
        companyId,
        companyNames,
        lower(dateSortDirection),
        lower(nextDateSortDirection),
        lower(statusSortDirection),
        lower(nextStatusSortDirection),
        statusTypeFilter,
        allCount,
        draftCount,
        currentCount,
        completedCount,
        currentPage, filteredApplicationViews.size() / 10 + 1,
        start + 1,
        max,
        filteredApplicationViews.size());
    return ok(applicationList.render(activeTab, applicationListView));
  }

  public Result licenceList(String activeTab) {
    if (activeTab == null) {
      activeTab = "siels";
    }
    //String tab = setOrRestoreTabState("licenceList", activeTab, "siels");
    return ok(licenceList.render(activeTab));
  }

  public Result licenceDetails(String licenceRef) {
    return ok(licenceDetails.render(licenceRef));
  }

  private String lower(SortDirection sortDirection) {
    if (sortDirection == null) {
      return null;
    } else {
      return sortDirection.toString().toLowerCase();
    }
  }

}

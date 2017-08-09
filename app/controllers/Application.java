package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Inject;
import components.service.ApplicationItemViewService;
import components.util.EnumUtil;
import models.enums.SortDirection;
import models.enums.StatusType;
import models.enums.StatusTypeFilter;
import models.view.ApplicationItemView;
import models.view.ApplicationListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import views.html.applicationList;
import views.html.licenceDetails;
import views.html.licenceList;

import java.util.List;
import java.util.stream.Collectors;

public class Application extends Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
  private static final ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();
  private final ApplicationItemViewService applicationItemViewService;

  @Inject
  public Application(ApplicationItemViewService applicationItemViewService) {
    this.applicationItemViewService = applicationItemViewService;
  }

  public String setOrRestoreTabState(String tabGroup, String activeTab, String defaultTab) {
    if (activeTab == null) {
      String sessionActiveTab = session(tabGroup + "ActiveTab");
      activeTab = (sessionActiveTab != null) ? sessionActiveTab : defaultTab;
    }
    session(tabGroup + "ActiveTab", activeTab);
    return activeTab;
  }

  public Result index() {
    return redirect("/applications");
  }

  public Result applicationList(String activeTab, Option<String> date, Option<String> status, Option<String> show) throws JsonProcessingException {

    StatusTypeFilter statusTypeFilter = EnumUtil.parse(StatusTypeFilter.class, show, StatusTypeFilter.ALL);

    SortDirection dateSortDirection = EnumUtil.parse(SortDirection.class, date, null);
    SortDirection statusSortDirection = EnumUtil.parse(SortDirection.class, status, null);
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
    String tab = setOrRestoreTabState("applicationList", activeTab, "created-by-you");

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

    ApplicationListView applicationListView = new ApplicationListView(filteredApplicationViews,
        lower(dateSortDirection),
        lower(nextDateSortDirection),
        lower(statusSortDirection),
        lower(nextStatusSortDirection),
        statusTypeFilter,
        allCount,
        draftCount,
        currentCount,
        completedCount);

    return ok(applicationList.render(tab, applicationListView));

  }

  public Result licenceList(String activeTab) {
    String tab = setOrRestoreTabState("licenceList", activeTab, "siels");
    return ok(licenceList.render(tab));
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

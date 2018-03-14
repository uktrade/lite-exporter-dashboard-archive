package controllers;

import com.google.inject.Inject;
import components.service.AppDataService;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationTabsViewService;
import components.service.ReadDataService;
import components.service.StatusTrackerViewService;
import components.service.UserService;
import models.AppData;
import models.ReadData;
import models.view.ApplicationSummaryView;
import models.view.ApplicationTabsView;
import models.view.StatusTrackerView;
import play.mvc.Result;
import play.mvc.With;
import views.html.statusTrackerTab;

@With(AppGuardAction.class)
public class StatusTabController extends SamlController {

  private final StatusTrackerViewService statusTrackerViewService;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final AppDataService appDataService;
  private final ApplicationTabsViewService applicationTabsViewService;
  private final UserService userService;
  private final ReadDataService readDataService;
  private final statusTrackerTab statusTrackerTab;

  @Inject
  public StatusTabController(StatusTrackerViewService statusTrackerViewService,
                             ApplicationSummaryViewService applicationSummaryViewService,
                             AppDataService appDataService,
                             ApplicationTabsViewService applicationTabsViewService,
                             UserService userService,
                             ReadDataService readDataService,
                             statusTrackerTab statusTrackerTab) {
    this.statusTrackerViewService = statusTrackerViewService;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.appDataService = appDataService;
    this.applicationTabsViewService = applicationTabsViewService;
    this.userService = userService;
    this.readDataService = readDataService;
    this.statusTrackerTab = statusTrackerTab;
  }

  public Result showStatusTab(String appId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    ReadData readData = readDataService.getReadData(userId, appData);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appData);
    ApplicationTabsView applicationTabsView = applicationTabsViewService.getApplicationTabsView(appData, readData);
    StatusTrackerView statusTrackerView = statusTrackerViewService.getStatusTrackerView(appData);
    return ok(statusTrackerTab.render(applicationSummaryView, applicationTabsView, statusTrackerView));
  }

}

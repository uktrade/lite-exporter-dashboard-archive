package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.service.AppDataService;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationTabsViewService;
import components.service.ReadDataService;
import components.service.StatusTrackerViewService;
import models.view.StatusTrackerView;
import components.service.UserService;
import models.AppData;
import models.ReadData;
import models.view.ApplicationSummaryView;
import models.view.ApplicationTabsView;
import play.mvc.Result;
import play.mvc.With;
import views.html.statusTrackerTab;

@With(AppGuardAction.class)
public class StatusTabController extends SamlController {

  private final String licenceApplicationAddress;
  private final StatusTrackerViewService statusTrackerViewService;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final AppDataService appDataService;
  private final ApplicationTabsViewService applicationTabsViewService;
  private final UserService userService;
  private final ReadDataService readDataService;

  @Inject
  public StatusTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                             StatusTrackerViewService statusTrackerViewService,
                             ApplicationSummaryViewService applicationSummaryViewService,
                             AppDataService appDataService,
                             ApplicationTabsViewService applicationTabsViewService,
                             UserService userService,
                             ReadDataService readDataService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.statusTrackerViewService = statusTrackerViewService;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.appDataService = appDataService;
    this.applicationTabsViewService = applicationTabsViewService;
    this.userService = userService;
    this.readDataService = readDataService;
  }

  public Result showStatusTab(String appId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    ReadData readData = readDataService.getReadData(userId, appData);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appData);
    ApplicationTabsView applicationTabsView = applicationTabsViewService.getApplicationTabsView(appData, readData);
    StatusTrackerView statusTrackerView = statusTrackerViewService.getStatusTrackerView(appData);
    return ok(statusTrackerTab.render(licenceApplicationAddress, applicationSummaryView, applicationTabsView, statusTrackerView));
  }

}

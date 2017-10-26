package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.service.AppDataService;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationTabsViewService;
import components.service.ReadDataService;
import components.service.StatusItemViewService;
import components.service.UserService;
import java.util.List;
import models.AppData;
import models.ReadData;
import models.view.ApplicationSummaryView;
import models.view.ApplicationTabsView;
import models.view.StatusItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Result;
import play.mvc.With;
import views.html.statusTrackerTab;

@With(AppGuardAction.class)
public class StatusTabController extends SamlController {

  private final String licenceApplicationAddress;
  private final StatusItemViewService statusItemViewService;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final AppDataService appDataService;
  private final ApplicationTabsViewService applicationTabsViewService;
  private final UserService userService;
  private final ReadDataService readDataService;

  private static final Logger LOGGER = LoggerFactory.getLogger(StatusTabController.class);
  private static final ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();

  @Inject
  public StatusTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                             StatusItemViewService statusItemViewService,
                             ApplicationSummaryViewService applicationSummaryViewService,
                             AppDataService appDataService,
                             ApplicationTabsViewService applicationTabsViewService,
                             UserService userService,
                             ReadDataService readDataService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.statusItemViewService = statusItemViewService;
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
    List<StatusItemView> statusItemViewList = statusItemViewService.getStatusItemViews(appData);
    return ok(statusTrackerTab.render(licenceApplicationAddress, applicationSummaryView, applicationTabsView, statusItemViewList));
  }

}

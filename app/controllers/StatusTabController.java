package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.service.ApplicationSummaryViewService;
import components.service.RfiViewService;
import components.service.StatusItemViewService;
import models.view.ApplicationSummaryView;
import models.view.StatusItemView;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.licenceApplicationTabs.statusTrackerTab;

import java.util.List;

public class StatusTabController extends Controller {

  private final String licenceApplicationAddress;
  private final StatusItemViewService statusItemViewService;
  private final RfiViewService rfiViewService;
  private final ApplicationSummaryViewService applicationSummaryViewService;


  @Inject
  public StatusTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                             StatusItemViewService statusItemViewService,
                             RfiViewService rfiViewService,
                             ApplicationSummaryViewService applicationSummaryViewService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.statusItemViewService = statusItemViewService;
    this.rfiViewService = rfiViewService;
    this.applicationSummaryViewService = applicationSummaryViewService;
  }

  public Result showStatusTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    int rfiViewCount = rfiViewService.getRfiViewCount(appId);
    List<StatusItemView> statusItemViewList = statusItemViewService.getStatusItemViews(appId);
    return ok(statusTrackerTab.render(licenceApplicationAddress, applicationSummaryView, rfiViewCount, statusItemViewList));
  }

}

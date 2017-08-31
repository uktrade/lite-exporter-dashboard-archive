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
import views.html.licenceApplicationTabs.outcomeDocsTab;
import views.html.licenceApplicationTabs.statusTrackerTab;

import java.util.List;

public class ApplicationDetailsController extends Controller {

  private final String licenceApplicationAddress;
  private final StatusItemViewService statusItemViewService;
  private final RfiViewService rfiViewService;
  private final ApplicationSummaryViewService applicationSummaryViewService;


  @Inject
  public ApplicationDetailsController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                                      StatusItemViewService statusItemViewService,
                                      RfiViewService rfiViewService,
                                      ApplicationSummaryViewService applicationSummaryViewService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.statusItemViewService = statusItemViewService;
    this.rfiViewService = rfiViewService;
    this.applicationSummaryViewService = applicationSummaryViewService;
  }

  public Result statusTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    List<StatusItemView> statusItemViewList = statusItemViewService.getStatusItemViews(appId);
    return ok(statusTrackerTab.render(licenceApplicationAddress, applicationSummaryView, getRfiViewCount(appId), statusItemViewList));
  }


  public Result outcomeTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    return ok(outcomeDocsTab.render(licenceApplicationAddress, applicationSummaryView, getRfiViewCount(appId)));
  }

  private int getRfiViewCount(String appId) {
    return rfiViewService.getRfiViewCount(appId);
  }

}

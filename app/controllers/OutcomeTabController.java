package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.service.ApplicationService;
import components.service.ApplicationSummaryViewService;
import components.service.RfiViewService;
import models.view.ApplicationSummaryView;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.outcomeDocsTab;

public class OutcomeTabController extends Controller {

  private final String licenceApplicationAddress;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final RfiViewService rfiViewService;
  private final ApplicationService applicationService;

  @Inject
  public OutcomeTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                              ApplicationSummaryViewService applicationSummaryViewService,
                              RfiViewService rfiViewService,
                              ApplicationService applicationService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.rfiViewService = rfiViewService;
    this.applicationService = applicationService;
  }

  public Result showOutcomeTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    int rfiViewCount = rfiViewService.getRfiViewCount(appId);
    boolean showDocuments = !applicationService.isApplicationInProgress(appId);
    return ok(outcomeDocsTab.render(licenceApplicationAddress, applicationSummaryView, rfiViewCount, showDocuments));
  }

}

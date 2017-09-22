package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.dao.OutcomeDao;
import components.service.ApplicationService;
import components.service.ApplicationSummaryViewService;
import components.service.RfiViewService;
import components.util.SortUtil;
import components.util.TimeUtil;
import models.Document;
import models.Outcome;
import models.view.ApplicationSummaryView;
import models.view.OutcomeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Result;
import views.html.outcomeDocsTab;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OutcomeTabController extends SamlController {

  private static final Logger LOGGER = LoggerFactory.getLogger(OutcomeTabController.class);

  private final String licenceApplicationAddress;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final RfiViewService rfiViewService;
  private final ApplicationService applicationService;
  private final OutcomeDao outcomeDao;

  @Inject
  public OutcomeTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                              ApplicationSummaryViewService applicationSummaryViewService,
                              RfiViewService rfiViewService,
                              ApplicationService applicationService, OutcomeDao outcomeDao) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.rfiViewService = rfiViewService;
    this.applicationService = applicationService;
    this.outcomeDao = outcomeDao;
  }

  public Result showOutcomeTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    int rfiViewCount = rfiViewService.getRfiViewCount(appId);
    boolean showDocuments = !applicationService.isApplicationInProgress(appId);
    List<OutcomeView> outcomeViews = getOutcomeViews(appId);
    return ok(outcomeDocsTab.render(licenceApplicationAddress, applicationSummaryView, rfiViewCount, showDocuments, outcomeViews));
  }

  private List<OutcomeView> getOutcomeViews(String appId) {
    List<OutcomeView> outcomeViews = new ArrayList<>();
    List<Outcome> outcomes = outcomeDao.getOutcomes(appId);
    outcomes.forEach(outcome -> SortUtil.sortDocuments(outcome.getDocuments()));
    SortUtil.sortOutcomes(outcomes);
    for (int i = 0; i < outcomes.size(); i++) {
      Outcome outcome = outcomes.get(i);
      List<String> documents = outcome.getDocuments().stream()
          .map(Document::getLicenceRef)
          .collect(Collectors.toList());
      String issuedOn = TimeUtil.formatDate(outcome.getCreatedTimestamp());
      String voidedOn;
      if (i > 0) {
        voidedOn = TimeUtil.formatDate(outcomes.get(i - 1).getCreatedTimestamp());
      } else {
        voidedOn = null;
      }
      OutcomeView outcomeView = new OutcomeView(issuedOn, voidedOn, documents);
      outcomeViews.add(outcomeView);
    }
    return outcomeViews;
  }

}

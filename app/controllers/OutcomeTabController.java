package controllers;

import com.google.inject.Inject;
import components.dao.WithdrawalApprovalDao;
import components.service.AppDataService;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationTabsViewService;
import components.service.ReadDataService;
import components.service.UserService;
import components.util.ApplicationUtil;
import components.util.Comparators;
import components.util.SortUtil;
import components.util.TimeUtil;
import models.AppData;
import models.Notification;
import models.Outcome;
import models.ReadData;
import models.view.ApplicationSummaryView;
import models.view.ApplicationTabsView;
import models.view.InformLetterSectionView;
import models.view.InformLetterView;
import models.view.OutcomeDocumentView;
import models.view.OutcomeView;
import play.mvc.Result;
import play.mvc.With;
import views.html.outcomeDocsTab;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@With(AppGuardAction.class)
public class OutcomeTabController extends SamlController {

  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final WithdrawalApprovalDao withdrawalApprovalDao;
  private final AppDataService appDataService;
  private final ApplicationTabsViewService applicationTabsViewService;
  private final UserService userService;
  private final ReadDataService readDataService;
  private final outcomeDocsTab outcomeDocsTab;

  @Inject
  public OutcomeTabController(ApplicationSummaryViewService applicationSummaryViewService,
                              WithdrawalApprovalDao withdrawalApprovalDao,
                              AppDataService appDataService,
                              ApplicationTabsViewService applicationTabsViewService,
                              UserService userService,
                              ReadDataService readDataService,
                              outcomeDocsTab outcomeDocsTab) {
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.withdrawalApprovalDao = withdrawalApprovalDao;
    this.appDataService = appDataService;
    this.applicationTabsViewService = applicationTabsViewService;
    this.userService = userService;
    this.readDataService = readDataService;
    this.outcomeDocsTab = outcomeDocsTab;
  }

  public Result showOutcomeTab(String appId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    ReadData readData = readDataService.getReadData(userId, appData);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appData);
    ApplicationTabsView applicationTabsView = applicationTabsViewService.getApplicationTabsView(appData, readData);
    boolean isInProgress = ApplicationUtil.isOriginalApplicationInProgress(appData);
    boolean isStopped = appData.getStopNotification() != null;
    boolean isWithdrawn = withdrawalApprovalDao.getWithdrawalApproval(appId) != null;
    List<OutcomeView> outcomeViews = getOutcomeViews(appData, readData);
    InformLetterSectionView informLetterSectionView = getInformLetterSectionView(appData, readData);
    readDataService.updateDocumentTabReadData(userId, appData, readData);
    return ok(outcomeDocsTab.render(applicationSummaryView, applicationTabsView, isInProgress, isStopped, isWithdrawn, outcomeViews, informLetterSectionView));
  }

  private List<OutcomeView> getOutcomeViews(AppData appData, ReadData readData) {
    List<Outcome> outcomes = ApplicationUtil.getAllOutcomes(appData);
    outcomes.sort(Comparators.OUTCOME_CREATED_REVERSED);
    List<OutcomeView> outcomeViews = new ArrayList<>();
    for (int i = 0; i < outcomes.size(); i++) {
      Outcome outcome = outcomes.get(i);
      boolean showNewIndicator = readData.getUnreadOutcomeIds().contains(outcome.getId());
      SortUtil.sortOutcomeDocuments(outcome.getOutcomeDocuments());
      List<OutcomeDocumentView> outcomeDocumentViews = outcome.getOutcomeDocuments().stream()
          .map(document -> {
            String name;
            if (document.getLicenceRef() != null) {
              name = document.getLicenceRef() + " " + document.getFilename();
            } else {
              name = document.getFilename();
            }
            return new OutcomeDocumentView(name, document.getUrl());
          })
          .collect(Collectors.toList());
      String issuedOn = TimeUtil.formatDate(outcome.getCreatedTimestamp());
      String voidedOn;
      if (i > 0) {
        voidedOn = TimeUtil.formatDate(outcomes.get(i - 1).getCreatedTimestamp());
      } else {
        voidedOn = null;
      }
      OutcomeView outcomeView = new OutcomeView(issuedOn, voidedOn, outcomeDocumentViews, showNewIndicator);
      outcomeViews.add(outcomeView);
    }
    return outcomeViews;
  }

  private InformLetterSectionView getInformLetterSectionView(AppData appData, ReadData readData) {
    List<InformLetterView> informLetterViews = ApplicationUtil.getAllInformNotifications(appData).stream()
        .sorted(Comparators.NOTIFICATION_CREATED_REVERSED)
        .map(this::getInformLetterView)
        .collect(Collectors.toList());
    boolean showNewIndicator = !readData.getUnreadInformNotificationIds().isEmpty();
    return new InformLetterSectionView(showNewIndicator, informLetterViews);
  }

  private InformLetterView getInformLetterView(Notification notification) {
    String name = TimeUtil.formatDate(notification.getCreatedTimestamp()) + " " + notification.getDocument().getFilename();
    String link = notification.getDocument().getUrl();
    return new InformLetterView(name, link);
  }

}

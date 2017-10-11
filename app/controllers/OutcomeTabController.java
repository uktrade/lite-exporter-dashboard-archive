package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.dao.NotificationDao;
import components.dao.WithdrawalApprovalDao;
import components.service.AppDataService;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationTabsViewService;
import components.service.ReadDataService;
import components.service.UserService;
import components.util.ApplicationUtil;
import components.util.Comparators;
import components.util.LinkUtil;
import components.util.SortUtil;
import components.util.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import models.AppData;
import models.Notification;
import models.NotificationType;
import models.Outcome;
import models.ReadData;
import models.view.ApplicationSummaryView;
import models.view.ApplicationTabsView;
import models.view.InformLetterSectionView;
import models.view.InformLetterView;
import models.view.OutcomeDocumentView;
import models.view.OutcomeView;
import play.mvc.Result;
import views.html.outcomeDocsTab;

public class OutcomeTabController extends SamlController {

  private final String licenceApplicationAddress;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final NotificationDao notificationDao;
  private final WithdrawalApprovalDao withdrawalApprovalDao;
  private final AppDataService appDataService;
  private final ApplicationTabsViewService applicationTabsViewService;
  private final UserService userService;
  private final ReadDataService readDataService;

  @Inject
  public OutcomeTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                              ApplicationSummaryViewService applicationSummaryViewService,
                              NotificationDao notificationDao,
                              WithdrawalApprovalDao withdrawalApprovalDao,
                              AppDataService appDataService,
                              ApplicationTabsViewService applicationTabsViewService,
                              UserService userService,
                              ReadDataService readDataService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.notificationDao = notificationDao;
    this.withdrawalApprovalDao = withdrawalApprovalDao;
    this.appDataService = appDataService;
    this.applicationTabsViewService = applicationTabsViewService;
    this.userService = userService;
    this.readDataService = readDataService;
  }

  public Result showOutcomeTab(String appId) {
    String userId = userService.getCurrentUserId();
    List<Notification> notifications = notificationDao.getNotifications(appId);
    AppData appData = appDataService.getAppData(appId);
    ReadData readData = readDataService.getReadData(userId, appData);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appData);
    ApplicationTabsView applicationTabsView = applicationTabsViewService.getApplicationTabsView(appData, readData);
    boolean isInProgress = ApplicationUtil.isApplicationInProgress(appData);
    boolean isStopped = notifications.stream()
        .anyMatch(notification -> notification.getNotificationType() == NotificationType.STOP);
    boolean isWithdrawn = withdrawalApprovalDao.getWithdrawalApproval(appId) != null;
    List<OutcomeView> outcomeViews = getOutcomeViews(appData, readData);
    InformLetterSectionView informLetterSectionView = getInformLetterSectionView(appData, readData);
    readDataService.updateDocumentTabReadData(userId, appData, readData);
    return ok(outcomeDocsTab.render(licenceApplicationAddress, applicationSummaryView, applicationTabsView, isInProgress, isStopped, isWithdrawn, outcomeViews, informLetterSectionView));
  }

  private List<OutcomeView> getOutcomeViews(AppData appData, ReadData readData) {
    List<OutcomeView> outcomeViews = new ArrayList<>();
    List<Outcome> outcomes = appData.getOutcomes().stream()
        .sorted(Comparators.OUTCOME_CREATED_REVERSED)
        .collect(Collectors.toList());
    outcomes.forEach(outcome -> SortUtil.sortDocuments(outcome.getDocuments()));
    for (int i = 0; i < outcomes.size(); i++) {
      Outcome outcome = outcomes.get(i);
      boolean showNewIndicator = !readData.getUnreadOutcomeIds().isEmpty();
      List<OutcomeDocumentView> outcomeDocumentViews = outcome.getDocuments().stream()
          .map(document -> new OutcomeDocumentView(document.getLicenceRef(), document.getUrl()))
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
    List<InformLetterView> informLetterViews = appData.getInformNotifications().stream()
        .sorted(Comparators.NOTIFICATION_CREATED_REVERSED)
        .map(this::getInformLetterView)
        .collect(Collectors.toList());
    boolean showNewIndicator = !readData.getUnreadInformNotificationIds().isEmpty();
    return new InformLetterSectionView(showNewIndicator, informLetterViews);
  }

  private InformLetterView getInformLetterView(Notification notification) {
    String name = notification.getDocument().getFilename();
    String link = notification.getDocument().getUrl();
    String anchor = LinkUtil.getInformLetterAnchor(notification);
    return new InformLetterView(name, link, anchor);
  }

}

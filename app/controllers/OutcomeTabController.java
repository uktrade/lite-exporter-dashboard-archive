package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.dao.NotificationDao;
import components.dao.OutcomeDao;
import components.dao.WithdrawalApprovalDao;
import components.service.ApplicationService;
import components.service.ApplicationSummaryViewService;
import components.service.RfiViewService;
import components.util.ApplicationUtil;
import components.util.SortUtil;
import components.util.TimeUtil;
import models.Notification;
import models.NotificationType;
import models.Outcome;
import models.view.ApplicationSummaryView;
import models.view.InformLetterView;
import models.view.OutcomeDocumentView;
import models.view.OutcomeView;
import play.mvc.Result;
import views.html.outcomeDocsTab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OutcomeTabController extends SamlController {

  private final String licenceApplicationAddress;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final RfiViewService rfiViewService;
  private final ApplicationService applicationService;
  private final OutcomeDao outcomeDao;
  private final NotificationDao notificationDao;
  private final WithdrawalApprovalDao withdrawalApprovalDao;

  @Inject
  public OutcomeTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                              ApplicationSummaryViewService applicationSummaryViewService,
                              RfiViewService rfiViewService,
                              ApplicationService applicationService,
                              OutcomeDao outcomeDao,
                              NotificationDao notificationDao,
                              WithdrawalApprovalDao withdrawalApprovalDao) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.rfiViewService = rfiViewService;
    this.applicationService = applicationService;
    this.outcomeDao = outcomeDao;
    this.notificationDao = notificationDao;
    this.withdrawalApprovalDao = withdrawalApprovalDao;
  }

  public Result showOutcomeTab(String appId) {
    List<Notification> notifications = notificationDao.getNotifications(appId);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    int rfiViewCount = rfiViewService.getRfiViewCount(appId);
    boolean isInProgress = applicationService.isApplicationInProgress(appId);
    boolean isStopped = notifications.stream()
        .anyMatch(notification -> notification.getNotificationType() == NotificationType.STOP);
    boolean isWithdrawn = withdrawalApprovalDao.getWithdrawalApproval(appId) != null;
    List<OutcomeView> outcomeViews = getOutcomeViews(appId);
    List<InformLetterView> informLetterViews = getInformLetterViews(notifications);
    return ok(outcomeDocsTab.render(licenceApplicationAddress, applicationSummaryView, rfiViewCount, isInProgress, isStopped, isWithdrawn, outcomeViews, informLetterViews));
  }

  private List<OutcomeView> getOutcomeViews(String appId) {
    List<OutcomeView> outcomeViews = new ArrayList<>();
    List<Outcome> outcomes = outcomeDao.getOutcomes(appId);
    outcomes.forEach(outcome -> SortUtil.sortDocuments(outcome.getDocuments()));
    SortUtil.reverseSortOutcomes(outcomes);
    for (int i = 0; i < outcomes.size(); i++) {
      Outcome outcome = outcomes.get(i);
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
      OutcomeView outcomeView = new OutcomeView(issuedOn, voidedOn, outcomeDocumentViews);
      outcomeViews.add(outcomeView);
    }
    return outcomeViews;
  }

  private List<InformLetterView> getInformLetterViews(List<Notification> notifications) {
    return notifications.stream()
        .filter(notification -> notification.getNotificationType() == NotificationType.INFORM)
        .sorted(Comparator.comparing(Notification::getCreatedTimestamp).reversed())
        .map(this::getInformLetterView)
        .collect(Collectors.toList());
  }

  private InformLetterView getInformLetterView(Notification notification) {
    String name = notification.getDocument().getFilename();
    String link = notification.getDocument().getUrl();
    String anchor = ApplicationUtil.getInformLetterAnchor(notification);
    return new InformLetterView(name, link, anchor);
  }

}

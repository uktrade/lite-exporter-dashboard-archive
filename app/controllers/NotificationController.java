package controllers;

import com.google.inject.Inject;
import components.service.AppDataService;
import components.util.ApplicationUtil;
import components.util.LinkUtil;
import models.AppData;
import models.Notification;
import models.WithdrawalApproval;
import play.mvc.Result;
import play.mvc.With;

import java.util.Optional;

@With(AppGuardAction.class)
public class NotificationController extends SamlController {

  private final AppDataService appDataService;

  @Inject
  public NotificationController(AppDataService appDataService) {
    this.appDataService = appDataService;
  }

  public Result viewNotification(String appId, String notificationId) {
    AppData appData = appDataService.getAppData(appId);
    String link = getLink(appData, notificationId);
    if (link != null) {
      return redirect(link);
    } else {
      return notFound("Unknown notification.");
    }
  }

  private String getLink(AppData appData, String notificationId) {
    return getRfiLink(appData, notificationId).orElse(
        getWithdrawalApprovalLink(appData, notificationId).orElse(
            getOutcomeLink(appData, notificationId).orElse(
                getDelayNotificationLink(appData, notificationId).orElse(
                    getStopNotificationLink(appData, notificationId).orElse(
                        getInformNotificationLink(appData, notificationId).orElse(
                            null))))));
  }

  private Optional<String> getRfiLink(AppData appData, String notificationId) {
    return ApplicationUtil.getAllRfi(appData).stream()
        .filter(rfi -> rfi.getId().equals(notificationId))
        .findAny()
        .map(rfi -> LinkUtil.getRfiLink(appData.getApplication().getId(), rfi.getId()));
  }

  private Optional<String> getWithdrawalApprovalLink(AppData appData, String notificationId) {
    WithdrawalApproval withdrawalApproval = appData.getWithdrawalApproval();
    if (withdrawalApproval != null && withdrawalApproval.getId().equals(notificationId)) {
      return Optional.of(LinkUtil.getWithdrawalApprovalMessageLink(withdrawalApproval));
    } else {
      return Optional.empty();
    }
  }

  private Optional<String> getOutcomeLink(AppData appData, String notificationId) {
    return ApplicationUtil.getAllOutcomes(appData).stream()
        .filter(outcome -> outcome.getId().equals(notificationId))
        .findAny()
        .map(outcome -> LinkUtil.getOutcomeDocumentsLink(appData.getApplication().getId()));
  }

  private Optional<String> getDelayNotificationLink(AppData appData, String notificationId) {
    Notification delayNotification = appData.getDelayNotification();
    if (delayNotification != null && delayNotification.getId().equals(notificationId)) {
      return Optional.of(LinkUtil.getDelayedMessageLink(appData.getApplication().getId(), delayNotification));
    } else {
      return Optional.empty();
    }
  }

  private Optional<String> getStopNotificationLink(AppData appData, String notificationId) {
    return ApplicationUtil.getAllStopNotifications(appData).stream()
        .filter(notification -> notification.getId().equals(notificationId))
        .findAny()
        .map(notification -> LinkUtil.getStoppedMessageLink(appData.getApplication().getId(), notification));
  }

  private Optional<String> getInformNotificationLink(AppData appData, String notificationId) {
    return ApplicationUtil.getAllInformNotifications(appData).stream()
        .filter(notification -> notification.getId().equals(notificationId))
        .findAny()
        .map(notification -> LinkUtil.getInformLettersLink(appData.getApplication().getId()));
  }

}

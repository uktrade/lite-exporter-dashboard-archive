package components.service;

import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.util.ApplicationUtil;
import components.util.Comparators;
import components.util.LinkUtil;
import components.util.TimeUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.AppData;
import models.Application;
import models.AttentionTabNotificationViews;
import models.CaseData;
import models.DateColumnInfo;
import models.Notification;
import models.Outcome;
import models.ReadData;
import models.Rfi;
import models.StatusColumnInfo;
import models.StatusUpdate;
import models.User;
import models.WithdrawalRejection;
import models.WithdrawalRequest;
import models.enums.ApplicationProgress;
import models.enums.EventLabelType;
import models.enums.StatusType;
import models.view.ApplicationItemView;
import models.view.NotificationView;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.bis.lite.customer.api.view.CustomerView;

public class ApplicationItemViewServiceImpl implements ApplicationItemViewService {

  private final CustomerServiceClient customerServiceClient;
  private final UserService userService;
  private final AppDataService appDataService;
  private final ReadDataService readDataService;
  private final UserPermissionService userPermissionService;

  @Inject
  public ApplicationItemViewServiceImpl(CustomerServiceClient customerServiceClient,
                                        UserService userService,
                                        AppDataService appDataService,
                                        ReadDataService readDataService,
                                        UserPermissionService userPermissionService) {
    this.customerServiceClient = customerServiceClient;
    this.userService = userService;
    this.appDataService = appDataService;
    this.readDataService = readDataService;
    this.userPermissionService = userPermissionService;
  }

  @Override
  public List<ApplicationItemView> getApplicationItemViews(String userId) {
    List<String> customerIds = userPermissionService.getCustomerIdsWithViewingPermission(userId);

    Map<String, String> customerIdToCompanyName = customerIds.stream()
        .map(customerServiceClient::getCustomer)
        .collect(Collectors.toMap(CustomerView::getCustomerId, CustomerView::getCompanyName));

    List<AppData> appDataList = appDataService.getAppDataList(customerIds);

    Map<String, ReadData> readDataMap = readDataService.getReadData(userId, appDataList);

    return appDataList.stream()
        .map(appData -> {
          String companyName = customerIdToCompanyName.get(appData.getApplication().getCustomerId());
          ReadData readData = readDataMap.get(appData.getApplication().getId());
          return getApplicationItemView(userId, appData, readData, companyName);
        }).collect(Collectors.toList());
  }

  private ApplicationItemView getApplicationItemView(String userId, AppData appData, ReadData readData, String companyName) {

    Application application = appData.getApplication();

    StatusColumnInfo statusColumnInfo = ApplicationUtil.getStatusInfo(appData);
    String applicationStatusDate = statusColumnInfo.getPrefix() + " " + TimeUtil.formatDate(statusColumnInfo.getApplicationStatusTimestamp());

    DateColumnInfo dateColumnInfo = getDateColumnInfo(appData);
    String date = TimeUtil.formatDate(dateColumnInfo.getDateTimestamp());

    String createdById = application.getCreatedByUserId();
    User user = userService.getUser(createdById);
    String destination = ApplicationUtil.getDestinations(application);

    ApplicationProgress applicationProgress = getApplicationProgress(appData);

    List<NotificationView> notificationViews = getNotificationViews(appData, readData, applicationProgress);

    AttentionTabNotificationViews attentionTabNotificationViews = getAttentionTabNotificationViews(userId, appData, readData);
    List<NotificationView> forYourAttentionNotificationViews = getForYourAttentionNotificationViews(attentionTabNotificationViews);
    Long latestEventTimestamp = getMostRecentEventTimestamp(attentionTabNotificationViews);
    String latestEventDate;
    if (latestEventTimestamp != null) {
      latestEventDate = TimeUtil.formatDate(latestEventTimestamp);
    } else {
      latestEventDate = null;
    }

    return new ApplicationItemView(application.getId(),
        application.getCustomerId(),
        companyName,
        createdById,
        user.getFirstName(),
        user.getLastName(),
        dateColumnInfo.getDateStatus(),
        date,
        dateColumnInfo.getDateTimestamp(),
        appData.getCaseReference(),
        application.getApplicantReference(),
        applicationProgress,
        statusColumnInfo.getApplicationStatus(),
        applicationStatusDate,
        statusColumnInfo.getApplicationStatusTimestamp(),
        destination,
        notificationViews,
        forYourAttentionNotificationViews,
        latestEventTimestamp,
        latestEventDate);
  }

  private ApplicationProgress getApplicationProgress(AppData appData) {
    if (!appData.getCaseDataList().isEmpty()) {
      CaseData caseData = ApplicationUtil.getMostRecentCase(appData);
      if (caseData.getOutcome() != null || caseData.getStopNotification() != null) {
        return ApplicationProgress.COMPLETED;
      } else {
        return ApplicationProgress.CURRENT;
      }
    } else {
      StatusUpdate maxStatusUpdate = ApplicationUtil.getMaxStatusUpdate(appData.getStatusUpdates());
      if (appData.getWithdrawalApproval() != null || appData.getStopNotification() != null || (maxStatusUpdate != null && maxStatusUpdate.getStatusType() == StatusType.COMPLETE)) {
        return ApplicationProgress.COMPLETED;
      } else if (appData.getApplication().getSubmittedTimestamp() != null) {
        return ApplicationProgress.CURRENT;
      } else {
        return ApplicationProgress.DRAFT;
      }
    }
  }

  private DateColumnInfo getDateColumnInfo(AppData appData) {
    if (!appData.getCaseDataList().isEmpty()) {
      CaseData caseData = ApplicationUtil.getMostRecentCase(appData);
      if (caseData.getOutcome() != null) {
        return new DateColumnInfo(ApplicationUtil.COMPLETED, caseData.getOutcome().getCreatedTimestamp());
      } else if (caseData.getStopNotification() != null) {
        return new DateColumnInfo(ApplicationUtil.COMPLETED, caseData.getStopNotification().getCreatedTimestamp());
      } else {
        return new DateColumnInfo(ApplicationUtil.RE_OPENED, caseData.getCaseDetails().getCreatedTimestamp());
      }
    } else {
      StatusUpdate maxStatusUpdate = ApplicationUtil.getMaxStatusUpdate(appData.getStatusUpdates());
      if (appData.getWithdrawalApproval() != null) {
        return new DateColumnInfo(ApplicationUtil.COMPLETED, appData.getWithdrawalApproval().getCreatedTimestamp());
      } else if (appData.getStopNotification() != null) {
        return new DateColumnInfo(ApplicationUtil.COMPLETED, appData.getStopNotification().getCreatedTimestamp());
      } else if (maxStatusUpdate != null && maxStatusUpdate.getStatusType() == StatusType.COMPLETE) {
        return new DateColumnInfo(ApplicationUtil.SUBMITTED, maxStatusUpdate.getCreatedTimestamp());
      } else if (appData.getApplication().getSubmittedTimestamp() != null) {
        return new DateColumnInfo(ApplicationUtil.SUBMITTED, appData.getApplication().getSubmittedTimestamp());
      } else {
        return new DateColumnInfo(ApplicationUtil.CREATED, appData.getApplication().getCreatedTimestamp());
      }
    }
  }

  private List<NotificationView> getNotificationViews(AppData appData, ReadData readData, ApplicationProgress applicationProgress) {
    String appId = appData.getApplication().getId();
    List<NotificationView> notificationViews = new ArrayList<>();
    List<Rfi> openRfiList;
    if (!appData.getCaseDataList().isEmpty()) {
      CaseData caseData = ApplicationUtil.getMostRecentCase(appData);
      openRfiList = ApplicationUtil.getOpenRfiList(caseData);
    } else {
      openRfiList = ApplicationUtil.getOpenRfiList(appData);
    }
    openRfiList.stream()
        .sorted(Comparators.RFI_CREATED)
        .findFirst()
        .ifPresent(rfi -> {
          NotificationView notificationView = getRfiNotificationView(appId, rfi);
          notificationViews.add(notificationView);
        });
    if (appData.getCaseDataList().isEmpty()) {
      if (applicationProgress != ApplicationProgress.COMPLETED) {
        ApplicationUtil.getOpenWithdrawalRequests(appData).stream()
            .findFirst()
            .ifPresent(withdrawalRequest -> {
              NotificationView notificationView = getWithdrawalRequestedNotificationView(withdrawalRequest);
              notificationViews.add(notificationView);
            });
      }
      appData.getWithdrawalRejections().stream()
          .filter(withdrawalRejection -> readData.getUnreadWithdrawalRejectionIds().contains(withdrawalRejection.getId()))
          .sorted(Comparators.WITHDRAWAL_REJECTION_CREATED_REVERSED)
          .findFirst()
          .ifPresent(withdrawalRejection -> {
            NotificationView notificationView = getWithdrawalRejectedNotificationView(withdrawalRejection);
            notificationViews.add(notificationView);
          });
      if (readData.getUnreadWithdrawalApprovalId() != null) {
        NotificationView notificationView = getWithdrawalApprovalNotificationView(appData);
        notificationViews.add(notificationView);
      }
    }
    if (applicationProgress != ApplicationProgress.COMPLETED) {
      List<Notification> informNotifications;
      if (!appData.getCaseDataList().isEmpty()) {
        informNotifications = ApplicationUtil.getMostRecentCase(appData).getInformNotifications();
      } else {
        informNotifications = appData.getInformNotifications();
      }
      informNotifications.stream()
          .sorted(Comparators.NOTIFICATION_CREATED)
          .findFirst()
          .ifPresent(notification -> {
            NotificationView notificationView = getInformNotificationView(appId, notification);
            notificationViews.add(notificationView);
          });
    }
    notificationViews.sort(Comparators.LINK_TEXT);
    return notificationViews;
  }

  private AttentionTabNotificationViews getAttentionTabNotificationViews(String userId, AppData appData, ReadData readData) {
    String appId = appData.getApplication().getId();
    List<NotificationView> rfiNotificationViews = ApplicationUtil.getAllOpenRfiList(appData).stream()
        .filter(rfi -> rfi.getRecipientUserIds().contains(userId))
        .map(rfi -> getRfiNotificationView(appId, rfi))
        .collect(Collectors.toList());
    List<NotificationView> stopNotificationViews = ApplicationUtil.getAllStopNotifications(appData).stream()
        .filter(notification -> readData.getUnreadStopNotificationIds().contains(notification.getId()))
        .map(notification -> getStopNotificationView(appId, notification))
        .collect(Collectors.toList());
    List<NotificationView> withdrawalRejectionNotificationViews = appData.getWithdrawalRejections().stream()
        .filter(withdrawalRejection -> readData.getUnreadWithdrawalRejectionIds().contains(withdrawalRejection.getId()))
        .map(this::getWithdrawalRejectedNotificationView)
        .collect(Collectors.toList());
    List<NotificationView> informNotificationViews = ApplicationUtil.getAllInformNotifications(appData).stream()
        .filter(notification -> readData.getUnreadInformNotificationIds().contains(notification.getId()))
        .map(notification -> getInformNotificationView(appId, notification))
        .collect(Collectors.toList());
    List<NotificationView> outcomeNotificationViews = ApplicationUtil.getAllOutcomes(appData).stream()
        .filter(outcome -> readData.getUnreadOutcomeIds().contains(appData.getOutcome().getId()))
        .map(outcome -> getOutcomeNotificationView(appId, outcome))
        .collect(Collectors.toList());
    NotificationView delayNotificationView;
    if (readData.getUnreadDelayNotificationId() != null) {
      delayNotificationView = getDelayNotificationView(appId, appData.getDelayNotification());
    } else {
      delayNotificationView = null;
    }
    NotificationView withdrawalApprovalNotificationView;
    if (readData.getUnreadWithdrawalApprovalId() != null) {
      withdrawalApprovalNotificationView = getWithdrawalApprovalNotificationView(appData);
    } else {
      withdrawalApprovalNotificationView = null;
    }
    return new AttentionTabNotificationViews(rfiNotificationViews,
        withdrawalRejectionNotificationViews,
        informNotificationViews,
        outcomeNotificationViews,
        stopNotificationViews,
        withdrawalApprovalNotificationView,
        delayNotificationView);
  }

  private Long getMostRecentEventTimestamp(AttentionTabNotificationViews views) {
    List<NotificationView> notifications = new ArrayList<>();
    notifications.addAll(views.getRfiNotificationViews());
    notifications.addAll(views.getWithdrawalRejectionNotificationViews());
    notifications.addAll(views.getInformNotificationViews());
    notifications.addAll(views.getStopNotificationViews());
    notifications.addAll(views.getOutcomeNotificationViews());
    CollectionUtils.addIgnoreNull(notifications, views.getDelayNotificationView());
    CollectionUtils.addIgnoreNull(notifications, views.getWithdrawalApprovalNotificationView());
    if (notifications.isEmpty()) {
      return null;
    } else {
      return Collections.max(notifications, Comparators.NOTIFICATION_VIEW_CREATED).getCreatedTimestamp();
    }
  }

  private List<NotificationView> getForYourAttentionNotificationViews(AttentionTabNotificationViews views) {
    List<NotificationView> notificationViews = new ArrayList<>();
    views.getRfiNotificationViews().stream()
        .sorted(Comparators.NOTIFICATION_VIEW_CREATED)
        .findFirst()
        .ifPresent(notificationViews::add);
    views.getWithdrawalRejectionNotificationViews().stream()
        .sorted(Comparators.NOTIFICATION_VIEW_CREATED_REVERSED)
        .findFirst()
        .ifPresent(notificationViews::add);
    views.getInformNotificationViews().stream()
        .sorted(Comparators.NOTIFICATION_VIEW_CREATED_REVERSED)
        .findFirst()
        .ifPresent(notificationViews::add);
    views.getOutcomeNotificationViews().stream()
        .sorted(Comparators.NOTIFICATION_VIEW_CREATED_REVERSED)
        .findFirst()
        .ifPresent(notificationViews::add);
    views.getStopNotificationViews().stream()
        .sorted(Comparators.NOTIFICATION_VIEW_CREATED_REVERSED)
        .findFirst()
        .ifPresent(notificationViews::add);
    CollectionUtils.addIgnoreNull(notificationViews, views.getDelayNotificationView());
    CollectionUtils.addIgnoreNull(notificationViews, views.getWithdrawalApprovalNotificationView());
    notificationViews.sort(Comparators.LINK_TEXT);
    return notificationViews;
  }

  private NotificationView getRfiNotificationView(String appId, Rfi rfi) {
    String link = controllers.routes.RfiTabController.showRfiTab(appId).withFragment(rfi.getId()).toString();
    return new NotificationView(EventLabelType.RFI, "Request for information", link, null, rfi.getCreatedTimestamp());
  }

  private NotificationView getStopNotificationView(String appId, Notification notification) {
    String link = LinkUtil.getStoppedMessageLink(appId, notification);
    return new NotificationView(EventLabelType.STOPPED, "View reason for stop", link, null, notification.getCreatedTimestamp());
  }

  private NotificationView getWithdrawalRequestedNotificationView(WithdrawalRequest withdrawalRequest) {
    String link = LinkUtil.getWithdrawalRequestMessageLink(withdrawalRequest);
    return new NotificationView(EventLabelType.WITHDRAWAL_REQUESTED, "Withdrawal requested", link, null, withdrawalRequest.getCreatedTimestamp());
  }

  private NotificationView getWithdrawalApprovalNotificationView(AppData appData) {
    String link = LinkUtil.getWithdrawalApprovalMessageLink(appData.getWithdrawalApproval());
    return new NotificationView(EventLabelType.WITHDRAWAL_ACCEPTED, "Withdrawal accepted", link, null, appData.getWithdrawalApproval().getCreatedTimestamp());
  }

  private NotificationView getWithdrawalRejectedNotificationView(WithdrawalRejection withdrawalRejection) {
    String link = LinkUtil.getWithdrawalRejectionMessageLink(withdrawalRejection);
    return new NotificationView(EventLabelType.WITHDRAWAL_REJECTED, "Withdrawal rejected", link, null, withdrawalRejection.getCreatedTimestamp());
  }

  private NotificationView getInformNotificationView(String appId, Notification notification) {
    String link = LinkUtil.getInformLettersLink(appId);
    return new NotificationView(EventLabelType.INFORM_ISSUED, "Inform letter issued", link, null, notification.getCreatedTimestamp());
  }

  private NotificationView getOutcomeNotificationView(String appId, Outcome outcome) {
    String link = LinkUtil.getOutcomeDocumentsLink(appId);
    return new NotificationView(EventLabelType.DECISION, "View outcome documents", link, null, outcome.getCreatedTimestamp());
  }

  private NotificationView getDelayNotificationView(String appId, Notification notification) {
    String link = LinkUtil.getDelayedMessageLink(appId, notification);
    return new NotificationView(EventLabelType.DELAYED, "Apology for delay received", link, null, notification.getCreatedTimestamp());
  }

}

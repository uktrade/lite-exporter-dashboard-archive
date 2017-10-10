package components.service;

import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.util.ApplicationUtil;
import components.util.Comparators;
import components.util.LinkUtil;
import components.util.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.AppData;
import models.Application;
import models.Notification;
import models.Outcome;
import models.ReadData;
import models.Rfi;
import models.StatusUpdate;
import models.User;
import models.WithdrawalRejection;
import models.enums.ApplicationProgress;
import models.enums.EventLabelType;
import models.enums.StatusType;
import models.view.ApplicationItemView;
import models.view.NotificationView;
import uk.gov.bis.lite.customer.api.view.CustomerView;

public class ApplicationItemViewServiceImpl implements ApplicationItemViewService {

  private final CustomerServiceClient customerServiceClient;
  private final UserService userService;
  private final AppDataService appDataService;
  private final ReadDataService readDataService;

  @Inject
  public ApplicationItemViewServiceImpl(CustomerServiceClient customerServiceClient,
                                        UserService userService,
                                        AppDataService appDataService,
                                        ReadDataService readDataService) {
    this.customerServiceClient = customerServiceClient;
    this.userService = userService;
    this.appDataService = appDataService;
    this.readDataService = readDataService;
  }

  @Override
  public List<ApplicationItemView> getApplicationItemViews(String userId) {
    List<CustomerView> customerViews = customerServiceClient.getCustomers(userId);

    Map<String, String> customerIdToCompanyName = customerViews.stream()
        .collect(Collectors.toMap(CustomerView::getCustomerId, CustomerView::getCompanyName));

    List<String> customerIds = new ArrayList<>(customerIdToCompanyName.keySet());

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

    StatusUpdate maxStatusUpdate = ApplicationUtil.getMaxStatusUpdate(appData.getStatusUpdates());

    String applicationStatus = ApplicationUtil.getApplicationStatus(appData);
    long applicationStatusTimestamp = getApplicationStatusTimestamp(appData, maxStatusUpdate);
    String applicationStatusDate = getApplicationStatusDate(appData, maxStatusUpdate, applicationStatusTimestamp);

    Long dateTimestamp = getDateTimestamp(maxStatusUpdate, application);
    String date = TimeUtil.formatDate(dateTimestamp);

    String createdById = application.getCreatedByUserId();
    User user = userService.getUser(createdById);
    String destination = ApplicationUtil.getDestinations(application.getDestinationList());

    ApplicationProgress applicationProgress = getApplicationProgress(appData, maxStatusUpdate);

    List<NotificationView> notificationViews = getNotificationViews(appData, readData, applicationProgress);
    NotificationView forYourAttentionNotificationView = getForYourAttentionNotificationView(userId, appData, readData);

    String latestEventDate;
    if (forYourAttentionNotificationView != null) {
      latestEventDate = TimeUtil.formatDate(forYourAttentionNotificationView.getCreatedTimestamp());
    } else {
      latestEventDate = null;
    }

    return new ApplicationItemView(application.getId(),
        application.getCustomerId(),
        companyName,
        createdById,
        user.getFirstName(),
        user.getLastName(),
        dateTimestamp,
        date,
        application.getCaseReference(),
        application.getApplicantReference(),
        applicationProgress,
        applicationStatus,
        applicationStatusDate,
        applicationStatusTimestamp,
        destination,
        notificationViews,
        forYourAttentionNotificationView,
        latestEventDate);
  }

  private String getApplicationStatusDate(AppData appData, StatusUpdate maxStatusUpdate, long applicationStatusTimestamp) {
    if (appData.getWithdrawalApproval() != null || appData.getStopNotification() != null || (maxStatusUpdate != null && StatusType.COMPLETE == maxStatusUpdate.getStatusType())) {
      return "On " + TimeUtil.formatDate(applicationStatusTimestamp);
    } else {
      return "Since " + TimeUtil.formatDate(applicationStatusTimestamp);
    }
  }

  private Long getApplicationStatusTimestamp(AppData appData, StatusUpdate maxStatusUpdate) {
    if (appData.getWithdrawalApproval() != null) {
      return appData.getWithdrawalApproval().getCreatedTimestamp();
    } else if (appData.getStopNotification() != null) {
      return appData.getStopNotification().getCreatedTimestamp();
    } else if (maxStatusUpdate != null) {
      return maxStatusUpdate.getCreatedTimestamp();
    } else if (appData.getApplication().getSubmittedTimestamp() != null) {
      return appData.getApplication().getSubmittedTimestamp();
    } else {
      return appData.getApplication().getCreatedTimestamp();
    }
  }

  private ApplicationProgress getApplicationProgress(AppData appData, StatusUpdate maxStatusUpdate) {
    if (appData.getWithdrawalApproval() != null || appData.getStopNotification() != null || (maxStatusUpdate != null && maxStatusUpdate.getStatusType() == StatusType.COMPLETE)) {
      return ApplicationProgress.COMPLETED;
    } else if (appData.getApplication().getSubmittedTimestamp() != null) {
      return ApplicationProgress.CURRENT;
    } else {
      return ApplicationProgress.DRAFT;
    }
  }

  private Long getDateTimestamp(StatusUpdate maxStatusUpdate, Application application) {
    if (maxStatusUpdate != null && maxStatusUpdate.getStatusType() == StatusType.COMPLETE) {
      return maxStatusUpdate.getCreatedTimestamp();
    } else if (application.getSubmittedTimestamp() != null) {
      return application.getSubmittedTimestamp();
    } else {
      return application.getCreatedTimestamp();
    }
  }

  private List<NotificationView> getNotificationViews(AppData appData, ReadData readData, ApplicationProgress applicationProgress) {
    List<NotificationView> notificationViews = new ArrayList<>();
    ApplicationUtil.getOpenRfiList(appData)
        .stream()
        .sorted(Comparators.RFI_CREATED)
        .findFirst()
        .ifPresent(rfi -> {
          NotificationView notificationView = getRfiNotificationView(rfi);
          notificationViews.add(notificationView);
        });
    ApplicationUtil.getOpenWithdrawalRequests(appData).stream()
        .findFirst()
        .ifPresent(withdrawalRequest -> {
          String link = LinkUtil.getWithdrawalRequestMessageLink(withdrawalRequest);
          NotificationView notificationView = new NotificationView(EventLabelType.WITHDRAWAL_REQUESTED, "Withdrawal requested", link, null, null);
          notificationViews.add(notificationView);
        });
    appData.getWithdrawalRejections().stream()
        .filter(withdrawalRejection -> readData.getUnreadWithdrawalRejectionIds().contains(withdrawalRejection.getId()))
        .sorted(Comparators.WITHDRAWAL_REJECTION_CREATED_REVERSED)
        .findFirst()
        .ifPresent(withdrawalRejection -> {
          NotificationView notificationView = getWithdrawalRejectedNotificationView(withdrawalRejection);
          notificationViews.add(notificationView);
        });
    if (applicationProgress != ApplicationProgress.COMPLETED) {
      appData.getInformNotifications().stream()
          .sorted(Comparators.NOTIFICATION_CREATED)
          .findFirst()
          .ifPresent(notification -> {
            String link = LinkUtil.getInformLetterLink(notification);
            NotificationView notificationView = new NotificationView(EventLabelType.INFORM_ISSUED, "Inform letter issued", link, null, null);
            notificationViews.add(notificationView);
          });
    }
    notificationViews.sort(Comparators.LINK_TEXT);
    return notificationViews;
  }

  private NotificationView getForYourAttentionNotificationView(String userId, AppData appData, ReadData readData) {
    List<NotificationView> notificationViews = new ArrayList<>();
    ApplicationUtil.getOpenRfiList(appData).stream()
        .filter(rfi -> rfi.getRecipientUserIds().contains(userId))
        .sorted(Comparators.RFI_CREATED)
        .findFirst()
        .ifPresent(rfi -> {
          NotificationView notificationView = getRfiNotificationView(rfi);
          notificationViews.add(notificationView);
        });
    if (readData.getUnreadStopNotificationId() != null) {
      NotificationView notificationView = getStopNotificationView(appData.getStopNotification());
      notificationViews.add(notificationView);
    }
    appData.getWithdrawalRejections().stream()
        .filter(withdrawalRejection -> readData.getUnreadWithdrawalRejectionIds().contains(withdrawalRejection.getId()))
        .sorted(Comparators.WITHDRAWAL_REJECTION_CREATED_REVERSED)
        .findFirst()
        .ifPresent(withdrawalRejection -> {
          NotificationView notificationView = getWithdrawalRejectedNotificationView(withdrawalRejection);
          notificationViews.add(notificationView);
        });
    appData.getInformNotifications().stream()
        .filter(notification -> readData.getUnreadInformNotificationIds().contains(notification.getId()))
        .sorted(Comparators.NOTIFICATION_CREATED)
        .findFirst()
        .ifPresent(notification -> {
          NotificationView notificationView = getInformNotificationView(notification);
          notificationViews.add(notificationView);
        });
    appData.getOutcomes().stream()
        .filter(outcome -> readData.getUnreadOutcomeIds().contains(outcome.getId()))
        .sorted(Comparators.OUTCOME_CREATED)
        .findFirst()
        .ifPresent(outcome -> {
          NotificationView notificationView = getOutcomeNotificationView(outcome);
          notificationViews.add(notificationView);
        });
    if (readData.getUnreadDelayNotificationId() != null) {
      NotificationView notificationView = getDelayNotificationView(appData.getDelayNotification());
      notificationViews.add(notificationView);
    }
    notificationViews.sort(Comparators.NOTIFICATION_VIEW_CREATED_REVERSED);
    if (notificationViews.isEmpty()) {
      return null;
    } else {
      return notificationViews.get(0);
    }
  }

  private NotificationView getRfiNotificationView(Rfi rfi) {
    String link = controllers.routes.RfiTabController.showRfiTab(rfi.getAppId()).withFragment(rfi.getId()).toString();
    return new NotificationView(EventLabelType.RFI, "Request for information", link, null, rfi.getCreatedTimestamp());
  }

  private NotificationView getStopNotificationView(Notification notification) {
    String link = LinkUtil.getStoppedMessageLink(notification);
    return new NotificationView(EventLabelType.STOPPED, "View reason for stop", link, null, notification.getCreatedTimestamp());
  }

  private NotificationView getWithdrawalRejectedNotificationView(WithdrawalRejection withdrawalRejection) {
    String link = LinkUtil.getWithdrawalRejectionMessageLink(withdrawalRejection);
    return new NotificationView(EventLabelType.WITHDRAWAL_REJECTED, "Withdrawal rejected", link, null, withdrawalRejection.getCreatedTimestamp());
  }

  private NotificationView getInformNotificationView(Notification notification) {
    String link = LinkUtil.getInformLetterLink(notification);
    return new NotificationView(EventLabelType.INFORM_ISSUED, "Inform letter issued", link, null, notification.getCreatedTimestamp());
  }

  private NotificationView getOutcomeNotificationView(Outcome outcome) {
    String link = LinkUtil.getOutcomeDocumentsLink(outcome.getAppId());
    return new NotificationView(EventLabelType.DECISION, "View outcome documents", link, null, System.currentTimeMillis());
  }

  private NotificationView getDelayNotificationView(Notification notification) {
    String link = LinkUtil.getDelayedMessageLink(notification);
    return new NotificationView(EventLabelType.DELAYED, "Apology for delay received", link, null, notification.getCreatedTimestamp());
  }

}

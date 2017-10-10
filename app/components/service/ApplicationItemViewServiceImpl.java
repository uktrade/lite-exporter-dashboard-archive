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
import models.ReadData;
import models.StatusUpdate;
import models.User;
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
          return getApplicationItemView(appData, readData, companyName);
        }).collect(Collectors.toList());
  }

  private ApplicationItemView getApplicationItemView(AppData appData, ReadData readData, String companyName) {

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
        notificationViews
    );
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

  private List<NotificationView> getNotificationViews(AppData appData, ReadData readData, ApplicationProgress applicationProgress) {
    List<NotificationView> notificationViews = new ArrayList<>();

    ApplicationUtil.getOpenRfiList(appData)
        .stream()
        .sorted(Comparators.RFI_RECEIVED)
        .findFirst()
        .ifPresent(rfi -> {
          String link = controllers.routes.RfiTabController.showRfiTab(rfi.getAppId()).withFragment(rfi.getId()).toString();
          NotificationView notificationView = new NotificationView(EventLabelType.RFI, "Request for information", link, null, null);
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
          String link = LinkUtil.getWithdrawalRejectionMessageLink(withdrawalRejection);
          NotificationView notificationView = new NotificationView(EventLabelType.WITHDRAWAL_REJECTED, "Withdrawal rejected", link, null, null);
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

}

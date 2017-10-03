package components.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.dao.ApplicationDao;
import components.dao.NotificationDao;
import components.dao.StatusUpdateDao;
import components.util.ApplicationUtil;
import components.util.SortUtil;
import components.util.TimeUtil;
import models.Application;
import models.Notification;
import models.NotificationType;
import models.Rfi;
import models.StatusUpdate;
import models.User;
import models.WithdrawalApproval;
import models.WithdrawalInformation;
import models.enums.ApplicationProgress;
import models.enums.EventLabelType;
import models.enums.StatusType;
import models.view.ApplicationItemView;
import models.view.NotificationView;
import uk.gov.bis.lite.customer.api.view.CustomerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationItemViewServiceImpl implements ApplicationItemViewService {

  private final ApplicationDao applicationDao;
  private final StatusUpdateDao statusUpdateDao;
  private final CustomerServiceClient customerServiceClient;
  private final UserService userService;
  private final NotificationDao notificationDao;
  private final RfiService rfiService;
  private final WithdrawalService withdrawalService;

  @Inject
  public ApplicationItemViewServiceImpl(ApplicationDao applicationDao,
                                        StatusUpdateDao statusUpdateDao,
                                        CustomerServiceClient customerServiceClient,
                                        UserService userService,
                                        NotificationDao notificationDao,
                                        RfiService rfiService,
                                        WithdrawalService withdrawalService) {
    this.applicationDao = applicationDao;
    this.statusUpdateDao = statusUpdateDao;
    this.customerServiceClient = customerServiceClient;
    this.userService = userService;
    this.notificationDao = notificationDao;
    this.rfiService = rfiService;
    this.withdrawalService = withdrawalService;
  }

  @Override
  public List<ApplicationItemView> getApplicationItemViews(String userId) {
    List<CustomerView> customerViews = customerServiceClient.getCustomers(userId);

    Map<String, String> customerIdToCompanyName = customerViews.stream()
        .collect(Collectors.toMap(CustomerView::getCustomerId, CustomerView::getCompanyName));

    List<String> customerIds = new ArrayList<>(customerIdToCompanyName.keySet());

    List<Application> applications = applicationDao.getApplications(customerIds);
    List<String> appIds = applications.stream().map(Application::getAppId).collect(Collectors.toList());

    List<Notification> notifications = notificationDao.getNotifications(appIds);

    HashMap<String, Notification> appIdToStopNotification = new HashMap<>();
    notifications.stream()
        .filter(notification -> notification.getNotificationType() == NotificationType.STOP)
        .forEach(notification -> appIdToStopNotification.put(notification.getAppId(), notification));

    HashMultimap<String, Notification> appIdToInformNotificationMultimap = HashMultimap.create();
    notifications.stream()
        .filter(notification -> notification.getNotificationType() == NotificationType.INFORM)
        .forEach(notification -> appIdToInformNotificationMultimap.put(notification.getAppId(), notification));

    Map<String, WithdrawalInformation> appIdToWithdrawalInformationMap = withdrawalService.getAppIdToWithdrawalInformationMap(appIds);

    Multimap<String, StatusUpdate> appIdToStatusUpdateMultimap = HashMultimap.create();
    statusUpdateDao.getStatusUpdates(appIds).forEach(statusUpdate -> appIdToStatusUpdateMultimap.put(statusUpdate.getAppId(), statusUpdate));

    Map<String, String> appIdToOpenRfiIdMap = getAppIdToOpenRfiIdMap(appIds);

    return applications.stream()
        .map(application -> {
          String companyName = customerIdToCompanyName.get(application.getCompanyId());
          String appId = application.getAppId();
          Collection<StatusUpdate> statusUpdates = appIdToStatusUpdateMultimap.get(appId);
          String openRfiId = appIdToOpenRfiIdMap.get(appId);
          Notification stopNotification = appIdToStopNotification.get(appId);
          Collection<Notification> informNotifications = appIdToInformNotificationMultimap.get(appId);
          WithdrawalInformation withdrawalInformation = appIdToWithdrawalInformationMap.get(appId);
          return getApplicationItemView(application, companyName, statusUpdates, openRfiId, stopNotification, informNotifications, withdrawalInformation);
        })
        .collect(Collectors.toList());
  }

  private ApplicationItemView getApplicationItemView(Application application, String companyName, Collection<StatusUpdate> statusUpdates, String openRfiId, Notification stopNotification, Collection<Notification> informNotifications, WithdrawalInformation withdrawalInformation) {
    StatusUpdate maxStatusUpdate = ApplicationUtil.getMaxStatusUpdate(statusUpdates);

    WithdrawalApproval withdrawalApproval = withdrawalInformation.getWithdrawalApproval();

    String applicationStatus = ApplicationUtil.getApplicationStatus(application, maxStatusUpdate, stopNotification, withdrawalApproval);
    long applicationStatusTimestamp = getApplicationStatusTimestamp(application, maxStatusUpdate, stopNotification, withdrawalApproval);

    Long dateTimestamp = getDateTimestamp(maxStatusUpdate, application);
    String date = TimeUtil.formatDate(dateTimestamp);

    String applicationStatusDate;
    if (withdrawalApproval != null || stopNotification != null || (maxStatusUpdate != null && StatusType.COMPLETE == maxStatusUpdate.getStatusType())) {
      applicationStatusDate = "On " + TimeUtil.formatDate(applicationStatusTimestamp);
    } else {
      applicationStatusDate = "Since " + TimeUtil.formatDate(applicationStatusTimestamp);
    }

    String createdById = application.getCreatedBy();
    User user = userService.getUser(createdById);
    String destination = ApplicationUtil.getDestinations(application.getDestinationList());

    ApplicationProgress applicationProgress = getApplicationProgress(withdrawalApproval, stopNotification, maxStatusUpdate, application);

    List<NotificationView> notificationViews = getNotificationViews(application.getAppId(), openRfiId, withdrawalInformation, informNotifications, applicationProgress);

    return new ApplicationItemView(application.getAppId(),
        application.getCompanyId(),
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

  private Long getApplicationStatusTimestamp(Application application, StatusUpdate maxStatusUpdate, Notification stopNotification, WithdrawalApproval withdrawalApproval) {
    if (withdrawalApproval != null) {
      return withdrawalApproval.getCreatedTimestamp();
    } else if (stopNotification != null) {
      return stopNotification.getCreatedTimestamp();
    } else if (maxStatusUpdate != null) {
      return maxStatusUpdate.getCreatedTimestamp();
    } else if (application.getSubmittedTimestamp() != null) {
      return application.getSubmittedTimestamp();
    } else {
      return application.getCreatedTimestamp();
    }
  }

  private List<NotificationView> getNotificationViews(String appId, String openRfiId, WithdrawalInformation withdrawalInformation, Collection<Notification> informNotifications, ApplicationProgress applicationProgress) {
    List<NotificationView> notificationViews = new ArrayList<>();
    if (openRfiId != null) {
      String link = controllers.routes.RfiTabController.showRfiTab(appId).withFragment(openRfiId).toString();
      NotificationView notificationView = new NotificationView(EventLabelType.RFI, "Request for information", link, null, null);
      notificationViews.add(notificationView);
    }
    withdrawalInformation.getOpenWithdrawalRequests().stream()
        .findFirst()
        .ifPresent(withdrawalRequest -> {
          String link = ApplicationUtil.getWithdrawalRequestMessageLink(withdrawalRequest);
          NotificationView notificationView = new NotificationView(EventLabelType.WITHDRAWAL_REQUESTED, "Withdrawal requested", link, null, null);
          notificationViews.add(notificationView);
        });
    withdrawalInformation.getWithdrawalRejections().stream()
        .findFirst()
        .ifPresent(withdrawalRejection -> {
          String link = ApplicationUtil.getWithdrawalRejectionMessageLink(withdrawalRejection);
          NotificationView notificationView = new NotificationView(EventLabelType.WITHDRAWAL_REJECTED, "Withdrawal rejected", link, null, null);
          notificationViews.add(notificationView);
        });
    if (applicationProgress != ApplicationProgress.COMPLETED) {
      informNotifications.stream()
          .sorted(Comparator.comparing(Notification::getCreatedTimestamp))
          .findFirst()
          .ifPresent(notification -> {
            String link = ApplicationUtil.getInformLetterLink(notification);
            NotificationView notificationView = new NotificationView(EventLabelType.INFORM_ISSUED, "Inform letter issued", link, null, null);
            notificationViews.add(notificationView);
          });
    }
    SortUtil.sortNotificationViewsByLinkText(notificationViews);
    return notificationViews;
  }

  private ApplicationProgress getApplicationProgress(WithdrawalApproval withdrawalApproval, Notification stopNotification, StatusUpdate maxStatusUpdate, Application application) {
    if (withdrawalApproval != null || stopNotification != null || (maxStatusUpdate != null && maxStatusUpdate.getStatusType() == StatusType.COMPLETE)) {
      return ApplicationProgress.COMPLETED;
    } else if (application.getSubmittedTimestamp() != null) {
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

  private Map<String, String> getAppIdToOpenRfiIdMap(List<String> appIds) {
    List<Rfi> openRfiList = rfiService.getOpenRfiList(appIds);
    Multimap<String, Rfi> appIdToRfi = HashMultimap.create();
    openRfiList.forEach(rfi -> appIdToRfi.put(rfi.getAppId(), rfi));
    Map<String, String> appIdToOpenRfiIdMap = new HashMap<>();
    appIdToRfi.asMap().forEach((appId, rfiCollection) -> {
      String openRfiId = getOpenRfiId(rfiCollection);
      appIdToOpenRfiIdMap.put(appId, openRfiId);
    });
    return appIdToOpenRfiIdMap;
  }

  private String getOpenRfiId(Collection<Rfi> openRfiList) {
    return openRfiList.stream()
        .sorted(Comparator.comparing(Rfi::getReceivedTimestamp))
        .map(Rfi::getRfiId)
        .findFirst()
        .orElse(null);
  }

}

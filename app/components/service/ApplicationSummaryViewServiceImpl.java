package components.service;

import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.NotificationDao;
import components.dao.StatusUpdateDao;
import components.dao.WithdrawalApprovalDao;
import components.util.ApplicationUtil;
import components.util.TimeUtil;
import models.Application;
import models.Notification;
import models.NotificationType;
import models.StatusUpdate;
import models.WithdrawalApproval;
import models.view.ApplicationSummaryView;

import java.util.List;

public class ApplicationSummaryViewServiceImpl implements ApplicationSummaryViewService {

  private final StatusUpdateDao statusUpdateDao;
  private final ApplicationDao applicationDao;
  private final UserService userService;
  private final WithdrawalApprovalDao withdrawalApprovalDao;
  private final NotificationDao notificationDao;

  @Inject
  public ApplicationSummaryViewServiceImpl(StatusUpdateDao statusUpdateDao,
                                           ApplicationDao applicationDao,
                                           UserService userService,
                                           WithdrawalApprovalDao withdrawalApprovalDao,
                                           NotificationDao notificationDao) {
    this.statusUpdateDao = statusUpdateDao;
    this.applicationDao = applicationDao;
    this.userService = userService;
    this.withdrawalApprovalDao = withdrawalApprovalDao;
    this.notificationDao = notificationDao;
  }

  @Override
  public ApplicationSummaryView getApplicationSummaryView(String appId) {
    Application application = applicationDao.getApplication(appId);


    return new ApplicationSummaryView(application.getAppId(),
        application.getCaseReference(),
        application.getApplicantReference(),
        ApplicationUtil.getDestinations(application.getDestinationList()),
        getDateSubmitted(application),
        getApplicationStatus(application),
        getOfficerName(application));
  }

  private String getDateSubmitted(Application application) {
    return TimeUtil.formatDate(application.getSubmittedTimestamp());
  }

  private String getOfficerName(Application application) {
    if (application.getCaseOfficerId() != null) {
      return userService.getUsername(application.getCaseOfficerId());
    } else {
      return "Not yet assigned";
    }
  }

  private String getApplicationStatus(Application application) {
    String appId = application.getAppId();
    List<StatusUpdate> statusUpdates = statusUpdateDao.getStatusUpdates(appId);
    StatusUpdate maxStatusUpdate = ApplicationUtil.getMaxStatusUpdate(statusUpdates);
    WithdrawalApproval withdrawalApproval = withdrawalApprovalDao.getWithdrawalApproval(appId);
    Notification stopNotification = notificationDao.getNotifications(appId).stream()
        .filter(notification -> notification.getNotificationType() == NotificationType.STOP)
        .findFirst()
        .orElse(null);
    return ApplicationUtil.getApplicationStatus(application, maxStatusUpdate, stopNotification, withdrawalApproval);
  }

}

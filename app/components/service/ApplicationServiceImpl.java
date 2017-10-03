package components.service;

import com.google.inject.Inject;
import components.dao.NotificationDao;
import components.dao.StatusUpdateDao;
import components.dao.WithdrawalApprovalDao;
import models.NotificationType;
import models.enums.StatusType;

public class ApplicationServiceImpl implements ApplicationService {

  private final StatusUpdateDao statusUpdateDao;
  private final NotificationDao notificationDao;
  private final WithdrawalApprovalDao withdrawalApprovalDao;

  @Inject
  public ApplicationServiceImpl(StatusUpdateDao statusUpdateDao,
                                NotificationDao notificationDao,
                                WithdrawalApprovalDao withdrawalApprovalDao) {
    this.statusUpdateDao = statusUpdateDao;
    this.notificationDao = notificationDao;
    this.withdrawalApprovalDao = withdrawalApprovalDao;
  }

  @Override
  public boolean isApplicationInProgress(String appId) {
    boolean isStopped = notificationDao.getNotifications(appId).stream()
        .anyMatch(notification -> notification.getNotificationType() == NotificationType.STOP);
    boolean isWithdrawn = withdrawalApprovalDao.getWithdrawalApproval(appId) != null;
    boolean isComplete = statusUpdateDao.getStatusUpdates(appId).stream()
        .anyMatch(statusUpdate -> statusUpdate.getStatusType() == StatusType.COMPLETE);
    return !isStopped && !isWithdrawn && !isComplete;
  }

}

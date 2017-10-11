package components.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.NotificationDao;
import components.dao.OutcomeDao;
import components.dao.RfiDao;
import components.dao.RfiReplyDao;
import components.dao.RfiWithdrawalDao;
import components.dao.StatusUpdateDao;
import components.dao.WithdrawalApprovalDao;
import components.dao.WithdrawalRejectionDao;
import components.dao.WithdrawalRequestDao;
import components.exceptions.UnexpectedStateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.AppData;
import models.Application;
import models.Notification;
import models.Outcome;
import models.Rfi;
import models.RfiWithdrawal;
import models.StatusUpdate;
import models.WithdrawalApproval;
import models.WithdrawalRejection;
import models.RfiReply;
import models.WithdrawalRequest;

public class AppDataServiceImpl implements AppDataService {

  private final StatusUpdateDao statusUpdateDao;
  private final ApplicationDao applicationDao;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final WithdrawalRejectionDao withdrawalRejectionDao;
  private final WithdrawalApprovalDao withdrawalApprovalDao;
  private final NotificationDao notificationDao;
  private final RfiDao rfiDao;
  private final RfiReplyDao rfiReplyDao;
  private final RfiWithdrawalDao rfiWithdrawalDao;
  private final OutcomeDao outcomeDao;

  @Inject
  public AppDataServiceImpl(StatusUpdateDao statusUpdateDao,
                            ApplicationDao applicationDao,
                            WithdrawalRequestDao withdrawalRequestDao,
                            WithdrawalRejectionDao withdrawalRejectionDao,
                            WithdrawalApprovalDao withdrawalApprovalDao,
                            NotificationDao notificationDao,
                            RfiDao rfiDao,
                            RfiReplyDao rfiReplyDao,
                            RfiWithdrawalDao rfiWithdrawalDao,
                            OutcomeDao outcomeDao) {
    this.statusUpdateDao = statusUpdateDao;
    this.applicationDao = applicationDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.withdrawalRejectionDao = withdrawalRejectionDao;
    this.withdrawalApprovalDao = withdrawalApprovalDao;
    this.notificationDao = notificationDao;
    this.rfiDao = rfiDao;
    this.rfiReplyDao = rfiReplyDao;
    this.rfiWithdrawalDao = rfiWithdrawalDao;
    this.outcomeDao = outcomeDao;
  }

  @Override
  public List<AppData> getAppDataList(List<String> customerIds) {
    List<Application> applications = applicationDao.getApplications(customerIds);
    List<AppData> appDataList = getAppDataListFromApplications(applications);
    appDataList.forEach(this::verifyWithdrawalData);
    return appDataList;
  }

  @Override
  public AppData getAppData(String appId) {
    Application application = applicationDao.getApplication(appId);
    List<AppData> appDataList = getAppDataListFromApplications(Collections.singletonList(application));
    AppData appData = appDataList.get(0);
    verifyWithdrawalData(appData);
    return appData;
  }

  private List<AppData> getAppDataListFromApplications(List<Application> applications) {
    List<String> appIds = applications.stream()
        .map(Application::getId)
        .collect(Collectors.toList());

    Multimap<String, StatusUpdate> statusUpdateMultimap = HashMultimap.create();
    statusUpdateDao.getStatusUpdates(appIds).forEach(statusUpdate -> statusUpdateMultimap.put(statusUpdate.getAppId(), statusUpdate));

    Map<String, WithdrawalApproval> withdrawalApprovalMap = new HashMap<>();
    withdrawalApprovalDao.getWithdrawalApprovals(appIds).forEach(withdrawalApproval -> withdrawalApprovalMap.put(withdrawalApproval.getAppId(), withdrawalApproval));

    Multimap<String, WithdrawalRequest> withdrawalRequestMultimap = HashMultimap.create();
    withdrawalRequestDao.getWithdrawalRequestsByAppIds(appIds).forEach(withdrawalRequest -> withdrawalRequestMultimap.put(withdrawalRequest.getAppId(), withdrawalRequest));

    Multimap<String, WithdrawalRejection> withdrawalRejectionMultimap = HashMultimap.create();
    withdrawalRejectionDao.getWithdrawalRejectionsByAppIds(appIds).forEach(withdrawalRejection -> withdrawalRejectionMultimap.put(withdrawalRejection.getAppId(), withdrawalRejection));

    List<Rfi> rfiList = rfiDao.getRfiList(appIds);
    Multimap<String, Rfi> rfiMultimap = HashMultimap.create();
    rfiList.forEach(rfi -> rfiMultimap.put(rfi.getAppId(), rfi));

    Map<String, String> rfiIdToAppId = new HashMap<>();
    rfiList.forEach(rfi -> rfiIdToAppId.put(rfi.getId(), rfi.getAppId()));

    List<String> rfiIds = new ArrayList<>(rfiIdToAppId.keySet());

    Multimap<String, RfiReply> rfiReplyMultimap = HashMultimap.create();
    rfiReplyDao.getRfiReplies(rfiIds).forEach(rfiReply -> rfiReplyMultimap.put(rfiIdToAppId.get(rfiReply.getRfiId()), rfiReply));

    Multimap<String, RfiWithdrawal> rfiWithdrawalMultimap = HashMultimap.create();
    rfiWithdrawalDao.getRfiWithdrawals(rfiIds).forEach(rfiWithdrawal -> rfiWithdrawalMultimap.put(rfiIdToAppId.get(rfiWithdrawal.getRfiId()), rfiWithdrawal));

    HashMap<String, Notification> stopNotifications = new HashMap<>();
    HashMap<String, Notification> delayNotifications = new HashMap<>();
    Multimap<String, Notification> informNotifications = HashMultimap.create();

    notificationDao.getNotifications(appIds).forEach(notification -> {
      switch (notification.getNotificationType()) {
      case INFORM:
        informNotifications.put(notification.getAppId(), notification);
        break;
      case DELAY:
        delayNotifications.put(notification.getAppId(), notification);
        break;
      case STOP:
        stopNotifications.put(notification.getAppId(), notification);
        break;
      default:
        throw new UnexpectedStateException("Unexpected notification type " + notification.getNotificationType());
      }
    });

    Multimap<String, Outcome> outcomeMultiMap = HashMultimap.create();
    outcomeDao.getOutcomes(appIds).forEach(outcome -> outcomeMultiMap.put(outcome.getAppId(), outcome));

    return applications.stream().map(application -> {
      String appId = application.getId();
      return new AppData(application, new ArrayList<>(statusUpdateMultimap.get(appId)),
          new ArrayList<>(withdrawalRequestMultimap.get(appId)),
          new ArrayList<>(withdrawalRejectionMultimap.get(appId)),
          withdrawalApprovalMap.get(appId),
          new ArrayList<>(rfiMultimap.get(appId)),
          new ArrayList<>(rfiReplyMultimap.get(appId)),
          new ArrayList<>(rfiWithdrawalMultimap.get(appId)),
          delayNotifications.get(appId),
          stopNotifications.get(appId),
          new ArrayList<>(informNotifications.get(appId)),
          new ArrayList<>(outcomeMultiMap.get(appId)));
    }).collect(Collectors.toList());
  }

  private void verifyWithdrawalData(AppData appData) {
    if ((appData.getWithdrawalApproval() == null && appData.getWithdrawalRejections().size() > appData.getWithdrawalRequests().size()) ||
        (appData.getWithdrawalApproval() != null && appData.getWithdrawalRejections().size() + 1 > appData.getWithdrawalRequests().size())) {
      throw new UnexpectedStateException("There are more withdrawal responses than requests for appId " + appData.getApplication().getId());
    }
  }

}

package components.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.dao.AmendmentRequestDao;
import components.dao.ApplicationDao;
import components.dao.CaseDetailsDao;
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
import components.util.ApplicationUtil;
import components.util.Comparators;
import models.AmendmentRequest;
import models.AppData;
import models.Application;
import models.CaseData;
import models.CaseDetails;
import models.Notification;
import models.Outcome;
import models.Rfi;
import models.RfiReply;
import models.RfiWithdrawal;
import models.StatusUpdate;
import models.WithdrawalApproval;
import models.WithdrawalRejection;
import models.WithdrawalRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
  private final AmendmentRequestDao amendmentRequestDao;
  private final CaseDetailsDao caseDetailsDao;

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
                            OutcomeDao outcomeDao,
                            AmendmentRequestDao amendmentRequestDao,
                            CaseDetailsDao caseDetailsDao) {
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
    this.amendmentRequestDao = amendmentRequestDao;
    this.caseDetailsDao = caseDetailsDao;
  }

  @Override
  public List<AppData> getAppDataList(List<String> customerIds) {
    List<Application> applications = applicationDao.getApplicationsByCustomerIds(customerIds);
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
        .distinct()
        .collect(Collectors.toList());

    List<CaseDetails> caseDetailsList = caseDetailsDao.getCaseDetailsListByAppIds(appIds);

    List<String> caseReferences = caseDetailsList.stream()
        .map(CaseDetails::getCaseReference)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    Multimap<String, StatusUpdate> statusUpdateMultimap = HashMultimap.create();
    statusUpdateDao.getStatusUpdates(appIds).forEach(statusUpdate -> statusUpdateMultimap.put(statusUpdate.getAppId(), statusUpdate));

    Map<String, WithdrawalApproval> withdrawalApprovalMap = new HashMap<>();
    withdrawalApprovalDao.getWithdrawalApprovals(appIds).forEach(withdrawalApproval -> withdrawalApprovalMap.put(withdrawalApproval.getAppId(), withdrawalApproval));

    Multimap<String, WithdrawalRequest> withdrawalRequestMultimap = HashMultimap.create();
    withdrawalRequestDao.getWithdrawalRequestsByAppIds(appIds).forEach(withdrawalRequest -> withdrawalRequestMultimap.put(withdrawalRequest.getAppId(), withdrawalRequest));

    Multimap<String, WithdrawalRejection> withdrawalRejectionMultimap = HashMultimap.create();
    withdrawalRejectionDao.getWithdrawalRejectionsByAppIds(appIds).forEach(withdrawalRejection -> withdrawalRejectionMultimap.put(withdrawalRejection.getAppId(), withdrawalRejection));

    List<Rfi> rfiList = rfiDao.getRfiList(caseReferences);
    Multimap<String, Rfi> caseReferenceToRfiList = HashMultimap.create();
    rfiList.forEach(rfi -> caseReferenceToRfiList.put(rfi.getCaseReference(), rfi));

    Map<String, String> rfiIdToCaseReference = new HashMap<>();
    rfiList.forEach(rfi -> rfiIdToCaseReference.put(rfi.getId(), rfi.getCaseReference()));

    List<String> rfiIds = new ArrayList<>(rfiIdToCaseReference.keySet());
    Multimap<String, RfiReply> caseReferenceToRfiReplies = HashMultimap.create();
    rfiReplyDao.getRfiReplies(rfiIds).forEach(rfiReply -> caseReferenceToRfiReplies.put(rfiIdToCaseReference.get(rfiReply.getRfiId()), rfiReply));

    Multimap<String, RfiWithdrawal> caseReferenceToRfiWithdrawals = HashMultimap.create();
    rfiWithdrawalDao.getRfiWithdrawals(rfiIds).forEach(rfiWithdrawal -> caseReferenceToRfiWithdrawals.put(rfiIdToCaseReference.get(rfiWithdrawal.getRfiId()), rfiWithdrawal));

    Map<String, Notification> caseReferenceToStopNotification = new HashMap<>();
    Map<String, Notification> caseReferenceToDelayNotification = new HashMap<>();
    Multimap<String, Notification> caseReferenceToInformNotifications = HashMultimap.create();

    notificationDao.getNotifications(caseReferences).forEach(notification -> {
      switch (notification.getNotificationType()) {
        case INFORM:
          caseReferenceToInformNotifications.put(notification.getCaseReference(), notification);
          break;
        case DELAY:
          caseReferenceToDelayNotification.put(notification.getCaseReference(), notification);
          break;
        case STOP:
          caseReferenceToStopNotification.put(notification.getCaseReference(), notification);
          break;
        default:
          throw new UnexpectedStateException("Unexpected notification type " + notification.getNotificationType());
      }
    });

    Map<String, Outcome> caseReferenceToOutcome = new HashMap<>();
    outcomeDao.getOutcomes(caseReferences).forEach(outcome -> caseReferenceToOutcome.put(outcome.getCaseReference(), outcome));

    Multimap<String, AmendmentRequest> amendmentMultimap = HashMultimap.create();
    amendmentRequestDao.getAmendmentRequests(appIds).forEach(amendment -> amendmentMultimap.put(amendment.getAppId(), amendment));

    HashMultimap<String, CaseDetails> caseDetailsMultimap = HashMultimap.create();
    caseDetailsList.forEach(caseDetails -> caseDetailsMultimap.put(caseDetails.getAppId(), caseDetails));

    return applications.stream().map(application -> {
      String appId = application.getId();
      Collection<CaseDetails> allCaseDetails = caseDetailsMultimap.get(appId);
      String applicationCaseReference;
      if (!allCaseDetails.isEmpty()) {
        CaseDetails original = Collections.min(allCaseDetails, Comparators.CASE_DETAILS_CREATED);
        applicationCaseReference = original.getCaseReference();
      } else {
        applicationCaseReference = null;
      }

      List<CaseData> caseDataList = allCaseDetails.stream()
          .filter(caseDetails -> !caseDetails.getCaseReference().equals(applicationCaseReference))
          .map(caseDetails -> {
            String caseReference = caseDetails.getCaseReference();
            return new CaseData(caseDetails,
                new ArrayList<>(caseReferenceToRfiList.get(caseReference)),
                new ArrayList<>(caseReferenceToRfiReplies.get(caseReference)),
                new ArrayList<>(caseReferenceToRfiWithdrawals.get(caseReference)),
                new ArrayList<>(caseReferenceToInformNotifications.get(caseReference)),
                caseReferenceToStopNotification.get(caseReference),
                caseReferenceToOutcome.get(caseReference));
          })
          // Cases that have been created but have no data attached to them are omitted
          .filter(caseData -> ApplicationUtil.isCaseStarted(caseData) || ApplicationUtil.isCaseFinished(caseData))
          .collect(Collectors.toList());

      return new AppData(application,
          applicationCaseReference,
          new ArrayList<>(statusUpdateMultimap.get(appId)),
          new ArrayList<>(withdrawalRequestMultimap.get(appId)),
          new ArrayList<>(withdrawalRejectionMultimap.get(appId)),
          withdrawalApprovalMap.get(appId),
          new ArrayList<>(caseReferenceToRfiList.get(applicationCaseReference)),
          new ArrayList<>(caseReferenceToRfiReplies.get(applicationCaseReference)),
          new ArrayList<>(caseReferenceToRfiWithdrawals.get(applicationCaseReference)),
          caseReferenceToDelayNotification.get(applicationCaseReference),
          caseReferenceToStopNotification.get(applicationCaseReference),
          new ArrayList<>(caseReferenceToInformNotifications.get(applicationCaseReference)),
          caseReferenceToOutcome.get(applicationCaseReference),
          new ArrayList<>(amendmentMultimap.get(appId)),
          caseDataList);
    }).collect(Collectors.toList());
  }

  private void verifyWithdrawalData(AppData appData) {
    if ((appData.getWithdrawalApproval() == null && appData.getWithdrawalRejections().size() > appData.getWithdrawalRequests().size()) ||
        (appData.getWithdrawalApproval() != null && appData.getWithdrawalRejections().size() + 1 > appData.getWithdrawalRequests().size())) {
      throw new UnexpectedStateException("There are more withdrawal responses than requests for appId " + appData.getApplication().getId());
    }
  }

}

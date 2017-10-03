package components.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
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
import models.WithdrawalApproval;
import models.WithdrawalInformation;
import models.WithdrawalRejection;
import models.enums.EventLabelType;
import models.enums.StatusType;
import models.view.NotificationView;
import models.view.StatusItemView;
import org.apache.commons.collections4.ListUtils;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatusItemViewServiceImpl implements StatusItemViewService {

  private final StatusUpdateDao statusUpdateDao;
  private final ApplicationDao applicationDao;
  private final WithdrawalService withdrawalService;
  private final NotificationDao notificationDao;
  private final RfiService rfiService;

  @Inject
  public StatusItemViewServiceImpl(StatusUpdateDao statusUpdateDao,
                                   ApplicationDao applicationDao,
                                   WithdrawalService withdrawalService,
                                   NotificationDao notificationDao,
                                   RfiService rfiService) {
    this.statusUpdateDao = statusUpdateDao;
    this.applicationDao = applicationDao;
    this.withdrawalService = withdrawalService;
    this.notificationDao = notificationDao;
    this.rfiService = rfiService;
  }

  @Override
  public List<StatusItemView> getStatusItemViews(String appId) {
    Application application = applicationDao.getApplication(appId);
    StatusItemView draftStatusItemView = createDraftStatusItemView(application);
    StatusItemView submittedStatusItemView = createSubmittedStatusItemView(application);
    List<StatusItemView> updateStatusItemViews = createUpdateStatusItemViews(appId);

    List<StatusItemView> statusItemViews = new ArrayList<>();
    statusItemViews.add(draftStatusItemView);
    statusItemViews.add(submittedStatusItemView);
    statusItemViews.addAll(updateStatusItemViews);
    return statusItemViews;
  }

  private StatusItemView createDraftStatusItemView(Application application) {
    String status = ApplicationUtil.DRAFT;
    String statusExplanation = "";
    String processingLabel = "Finished";
    String processingDescription = "Created on " + TimeUtil.formatDate(application.getCreatedTimestamp());
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, new ArrayList<>());
  }

  private StatusItemView createSubmittedStatusItemView(Application application) {
    String status = ApplicationUtil.SUBMITTED;
    String statusExplanation = "";
    String processingLabel;
    String processingDescription;
    if (application.getSubmittedTimestamp() == null) {
      processingLabel = "Not started";
      processingDescription = "";
    } else {
      processingLabel = "Finished";
      processingDescription = "Submitted on " + TimeUtil.formatDate(application.getSubmittedTimestamp());
    }
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, new ArrayList<>());
  }

  private List<StatusItemView> createUpdateStatusItemViews(String appId) {
    WithdrawalInformation withdrawalInformation = withdrawalService.getWithdrawalInformation(appId);

    List<Notification> notifications = notificationDao.getNotifications(appId);
    Notification stopNotification = notifications.stream()
        .filter(notification -> notification.getNotificationType() == NotificationType.STOP)
        .findAny()
        .orElse(null);

    List<NotificationView> notificationViews = new ArrayList<>();
    notificationViews.addAll(getRfiNotificationViews(appId));
    notificationViews.addAll(getWithdrawalNotificationViews(withdrawalInformation));
    notificationViews.addAll(getDelayAndInformNotificationViews(notifications));
    if (stopNotification != null) {
      notificationViews.add(getStopNotificationView(stopNotification));
    }

    SortUtil.reverseSortNotificationViews(notificationViews);

    List<StatusUpdate> statusUpdates = getStatusUpdates(appId);

    Multimap<StatusUpdate, NotificationView> notificationViewMultimap = HashMultimap.create();
    for (NotificationView notificationView : notificationViews) {
      for (StatusUpdate statusUpdate : Lists.reverse(statusUpdates)) {
        if ((statusUpdate.getCreatedTimestamp() != null && notificationView.getCreatedTimestamp() >= statusUpdate.getCreatedTimestamp()) || statusUpdate.getStatusType() == StatusType.INITIAL_CHECKS) {
          notificationViewMultimap.put(statusUpdate, notificationView);
          break;
        }
      }
    }

    Map<StatusUpdate, Long> finishedTimestamps = getFinishedTimestamps(statusUpdates);

    List<StatusItemView> statusItemViews = statusUpdates.stream()
        .map(statusUpdate -> getStatusItemView(statusUpdate, new ArrayList<>(notificationViewMultimap.get(statusUpdate)), finishedTimestamps.get(statusUpdate)))
        .collect(Collectors.toList());

    WithdrawalApproval withdrawalApproval = withdrawalInformation.getWithdrawalApproval();
    if (withdrawalApproval != null || stopNotification != null) {
      int j = 0;
      for (int i = statusUpdates.size() - 1; i >= 0; i--) {
        StatusUpdate statusUpdate = statusUpdates.get(i);
        if (statusUpdate.getCreatedTimestamp() != null && finishedTimestamps.get(statusUpdate) == null) {
          j = i;
          break;
        }
      }
      StatusItemView inProgressStatusItemView = statusItemViews.get(j);
      if (withdrawalApproval != null) {
        StatusItemView withdrawalApprovalStatusItemView = getWithdrawalApprovalStatusItemView(withdrawalApproval, inProgressStatusItemView);
        statusItemViews.set(j, withdrawalApprovalStatusItemView);
      } else {
        StatusItemView stopStatusItemView = geStopStatusItemView(stopNotification, inProgressStatusItemView);
        statusItemViews.set(j, stopStatusItemView);
      }
    }

    return statusItemViews;
  }

  private Map<StatusUpdate, Long> getFinishedTimestamps(List<StatusUpdate> statusUpdates) {
    Map<StatusUpdate, Long> finishedTimestamps = new HashMap<>();
    for (int i = 0; i < statusUpdates.size() - 1; i++) {
      finishedTimestamps.put(statusUpdates.get(i), statusUpdates.get(i + 1).getCreatedTimestamp());
    }
    finishedTimestamps.put(statusUpdates.get(statusUpdates.size() - 1), statusUpdates.get(statusUpdates.size() - 1).getCreatedTimestamp());
    return finishedTimestamps;
  }

  private StatusItemView geStopStatusItemView(Notification notification, StatusItemView inProgressStatusItemView) {
    String processingDescription = "On " + TimeUtil.formatDate(notification.getCreatedTimestamp());
    return new StatusItemView(inProgressStatusItemView.getStatus(),
        inProgressStatusItemView.getStatusExplanation(),
        "Stopped",
        processingDescription,
        inProgressStatusItemView.getNotificationViews());
  }

  private StatusItemView getWithdrawalApprovalStatusItemView(WithdrawalApproval withdrawalApproval, StatusItemView inProgressStatusItemView) {
    String processingDescription = "On " + TimeUtil.formatDate(withdrawalApproval.getCreatedTimestamp());
    return new StatusItemView(inProgressStatusItemView.getStatus(),
        inProgressStatusItemView.getStatusExplanation(),
        "Withdrawn",
        processingDescription,
        inProgressStatusItemView.getNotificationViews());
  }

  private List<NotificationView> getWithdrawalNotificationViews(WithdrawalInformation withdrawalInformation) {
    List<NotificationView> notificationViews = new ArrayList<>();

    if (withdrawalInformation.getWithdrawalApproval() != null) {
      String link = ApplicationUtil.getWithdrawalRequestMessageLink(withdrawalInformation.getApprovedWithdrawalRequest());
      NotificationView withdrawalApprovalNotificationView = new NotificationView(null, "View withdrawal request", link, null, withdrawalInformation.getWithdrawalApproval().getCreatedTimestamp());
      notificationViews.add(withdrawalApprovalNotificationView);
    }

    List<NotificationView> withdrawalRequestNotificationViews = withdrawalInformation.getOpenWithdrawalRequests().stream()
        .map(this::getWithdrawalRequestNotificationView)
        .collect(Collectors.toList());
    notificationViews.addAll(withdrawalRequestNotificationViews);

    List<NotificationView> withdrawalRejectionNotificationViews = withdrawalInformation.getWithdrawalRejections().stream()
        .map(this::getWithdrawalRejectionNotificationView)
        .collect(Collectors.toList());
    notificationViews.addAll(withdrawalRejectionNotificationViews);

    return notificationViews;
  }

  private List<NotificationView> getDelayAndInformNotificationViews(List<Notification> notifications) {
    List<NotificationView> informNotificationViews = notifications.stream()
        .filter(notification -> notification.getNotificationType() == NotificationType.INFORM)
        .map(this::getInformNotificationView)
        .collect(Collectors.toList());
    List<NotificationView> delayedNotificationViews = notifications.stream()
        .filter(notification -> notification.getNotificationType() == NotificationType.DELAY)
        .map(this::getDelayNotificationView)
        .collect(Collectors.toList());
    return ListUtils.union(informNotificationViews, delayedNotificationViews);
  }

  private List<StatusUpdate> getStatusUpdates(String appId) {
    Map<StatusType, StatusUpdate> statusUpdateMap = new EnumMap<>(StatusType.class);
    statusUpdateDao.getStatusUpdates(appId).forEach(su -> statusUpdateMap.put(su.getStatusType(), su));
    return ApplicationUtil.getAscendingStatusTypeList().stream().map(statusType -> {
      StatusUpdate statusUpdate = statusUpdateMap.get(statusType);
      if (statusUpdate != null) {
        return statusUpdate;
      } else {
        return new StatusUpdate(appId, statusType, null);
      }
    }).collect(Collectors.toList());
  }

  private NotificationView getStopNotificationView(Notification notification) {
    String link = ApplicationUtil.getStoppedMessageLink(notification);
    return new NotificationView(null, "View reason for stop", link, null, notification.getCreatedTimestamp());
  }

  private NotificationView getDelayNotificationView(Notification notification) {
    String time = TimeUtil.formatDateAndTime(notification.getCreatedTimestamp());
    String description = "on " + time;
    String link = ApplicationUtil.getDelayedMessageLink(notification);
    return new NotificationView(EventLabelType.DELAYED, "Apology for delay received", link, description, notification.getCreatedTimestamp());
  }

  private NotificationView getInformNotificationView(Notification notification) {
    String time = TimeUtil.formatDateAndTime(notification.getCreatedTimestamp());
    String description = "on " + time;
    String link = ApplicationUtil.getInformLetterLink(notification);
    return new NotificationView(EventLabelType.INFORM_ISSUED, "Inform letter issued", link, description, notification.getCreatedTimestamp());
  }

  private NotificationView getWithdrawalRejectionNotificationView(WithdrawalRejection withdrawalRejection) {
    String time = TimeUtil.formatDateAndTime(withdrawalRejection.getCreatedTimestamp());
    String description = "on " + time;
    String link = ApplicationUtil.getWithdrawalRejectionMessageLink(withdrawalRejection);
    return new NotificationView(EventLabelType.WITHDRAWAL_REJECTED, "Withdrawal rejected", link, description, withdrawalRejection.getCreatedTimestamp());
  }

  private NotificationView getWithdrawalRequestNotificationView(WithdrawalRequest withdrawalRequest) {
    String time = TimeUtil.formatDateAndTime(withdrawalRequest.getCreatedTimestamp());
    String description = "sent on " + time;
    String link = ApplicationUtil.getWithdrawalRequestMessageLink(withdrawalRequest);
    return new NotificationView(EventLabelType.WITHDRAWAL_REQUESTED, "Withdrawal request", link, description, withdrawalRequest.getCreatedTimestamp());
  }

  private List<NotificationView> getRfiNotificationViews(String appId) {
    List<Rfi> rfiList = rfiService.getOpenRfiList(Collections.singletonList(appId));
    return rfiList.stream()
        .map(this::getRfiNotificationView)
        .collect(Collectors.toList());
  }

  private NotificationView getRfiNotificationView(Rfi rfi) {
    String time = TimeUtil.formatDateAndTime(rfi.getReceivedTimestamp());
    String description = "Received on " + time;
    String link = controllers.routes.RfiTabController.showRfiTab(rfi.getAppId()).withFragment(rfi.getRfiId()).toString();
    return new NotificationView(EventLabelType.RFI, "Request for information", link, description, rfi.getReceivedTimestamp());
  }

  private StatusItemView getStatusItemView(StatusUpdate statusUpdate, List<NotificationView> notificationViews, Long finishedTimestamp) {
    String status = ApplicationUtil.getStatusName(statusUpdate.getStatusType());
    String statusExplanation = ApplicationUtil.getStatusExplanation(statusUpdate.getStatusType());
    String processingLabel = getProcessingLabel(statusUpdate, finishedTimestamp);
    String processingDescription = getProcessingDescription(statusUpdate, finishedTimestamp);
    SortUtil.sortNotificationViewsByCreatedTimestamp(notificationViews);
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, notificationViews);
  }

  private String getProcessingLabel(StatusUpdate statusUpdate, Long finishedTimestamp) {
    if (finishedTimestamp != null) {
      return "Finished";
    } else if (statusUpdate.getCreatedTimestamp() == null) {
      return "Not started";
    } else {
      return "In progress";
    }
  }

  private String getProcessingDescription(StatusUpdate statusUpdate, Long finishedTimestamp) {
    if (statusUpdate.getStatusType() == StatusType.COMPLETE) {
      if (finishedTimestamp != null) {
        return "Completed on " + TimeUtil.formatDate(statusUpdate.getCreatedTimestamp());
      } else {
        return "";
      }
    } else {
      Long createdTimestamp = statusUpdate.getCreatedTimestamp();
      if (createdTimestamp != null) {
        if (finishedTimestamp != null) {
          long duration = TimeUtil.daysBetweenWithStartBeforeEnd(createdTimestamp, finishedTimestamp);
          return "Processed in " + duration + " working days";
        } else {
          String started = TimeUtil.formatDate(createdTimestamp);
          long duration = TimeUtil.daysBetweenWithStartBeforeEnd(createdTimestamp, Instant.now().toEpochMilli());
          return String.format("Started on %s<br>(%d days ago)", started, duration);
        }
      } else {
        return "";
      }
    }
  }

}

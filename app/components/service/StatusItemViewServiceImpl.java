package components.service;

import static components.util.RandomIdUtil.statusUpdateId;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.util.ApplicationUtil;
import components.util.Comparators;
import components.util.LinkUtil;
import components.util.TimeUtil;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.AppData;
import models.Application;
import models.Notification;
import models.Rfi;
import models.StatusUpdate;
import models.WithdrawalApproval;
import models.WithdrawalRejection;
import models.enums.EventLabelType;
import models.enums.StatusType;
import models.view.NotificationView;
import models.view.StatusItemView;
import models.WithdrawalRequest;

public class StatusItemViewServiceImpl implements StatusItemViewService {

  @Inject
  public StatusItemViewServiceImpl() {
  }

  @Override
  public List<StatusItemView> getStatusItemViews(AppData appData) {
    StatusItemView draftStatusItemView = createDraftStatusItemView(appData.getApplication());
    StatusItemView submittedStatusItemView = createSubmittedStatusItemView(appData.getApplication());
    List<StatusItemView> updateStatusItemViews = createUpdateStatusItemViews(appData);

    List<StatusItemView> statusItemViews = new ArrayList<>();
    statusItemViews.add(draftStatusItemView);
    statusItemViews.add(submittedStatusItemView);
    statusItemViews.addAll(updateStatusItemViews);
    return statusItemViews;
  }

  private StatusItemView createDraftStatusItemView(Application application) {
    String status = ApplicationUtil.DRAFT;
    String statusExplanation = "";
    String processingLabel = ApplicationUtil.FINISHED;
    String processingDescription = "Created on " + TimeUtil.formatDate(application.getCreatedTimestamp());
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, new ArrayList<>());
  }

  private StatusItemView createSubmittedStatusItemView(Application application) {
    String status = ApplicationUtil.SUBMITTED;
    String statusExplanation = "";
    String processingLabel;
    String processingDescription;
    if (application.getSubmittedTimestamp() == null) {
      processingLabel = ApplicationUtil.NOT_STARTED;
      processingDescription = "";
    } else {
      processingLabel = ApplicationUtil.FINISHED;
      processingDescription = "Submitted on " + TimeUtil.formatDate(application.getSubmittedTimestamp());
    }
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, new ArrayList<>());
  }

  private List<StatusItemView> createUpdateStatusItemViews(AppData appData) {

    List<NotificationView> notificationViews = getNotificationViews(appData);
    notificationViews.sort(Comparators.NOTIFICATION_VIEW_CREATED_REVERSED);

    List<StatusUpdate> statusUpdates = getStatusUpdates(appData.getApplication().getId(), appData.getStatusUpdates());

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

    WithdrawalApproval withdrawalApproval = appData.getWithdrawalApproval();
    if (withdrawalApproval != null || appData.getStopNotification() != null) {
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
        StatusItemView stopStatusItemView = geStopStatusItemView(appData.getStopNotification(), inProgressStatusItemView);
        statusItemViews.set(j, stopStatusItemView);
      }
    }

    return statusItemViews;
  }

  private List<NotificationView> getNotificationViews(AppData appData) {
    List<NotificationView> notificationViews = new ArrayList<>();

    notificationViews.addAll(getRfiNotificationViews(appData));

    notificationViews.addAll(getWithdrawalNotificationViews(appData));

    if (appData.getDelayNotification() != null) {
      notificationViews.add(getDelayNotificationView(appData.getDelayNotification()));
    }

    List<NotificationView> informNotificationViews = appData.getInformNotifications().stream()
        .map(this::getInformNotificationView)
        .collect(Collectors.toList());
    notificationViews.addAll(informNotificationViews);

    if (appData.getStopNotification() != null) {
      notificationViews.add(getStopNotificationView(appData.getStopNotification()));
    }

    if (!appData.getOutcomes().isEmpty()) {
      notificationViews.add(getOutcomeNotificationView(appData.getApplication().getId()));
    }
    return notificationViews;
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
        ApplicationUtil.STOPPED,
        processingDescription,
        inProgressStatusItemView.getNotificationViews());
  }

  private StatusItemView getWithdrawalApprovalStatusItemView(WithdrawalApproval withdrawalApproval, StatusItemView inProgressStatusItemView) {
    String processingDescription = "On " + TimeUtil.formatDate(withdrawalApproval.getCreatedTimestamp());
    return new StatusItemView(inProgressStatusItemView.getStatus(),
        inProgressStatusItemView.getStatusExplanation(),
        ApplicationUtil.WITHDRAWN,
        processingDescription,
        inProgressStatusItemView.getNotificationViews());
  }

  private List<NotificationView> getWithdrawalNotificationViews(AppData appData) {
    List<NotificationView> notificationViews = new ArrayList<>();

    if (appData.getWithdrawalApproval() != null) {
      WithdrawalRequest approvedWithdrawalRequest = ApplicationUtil.getApprovedWithdrawalRequest(appData);
      String link = LinkUtil.getWithdrawalRequestMessageLink(approvedWithdrawalRequest);
      NotificationView withdrawalApprovalNotificationView = new NotificationView(null, "View withdrawal request", link, null, appData.getWithdrawalApproval().getCreatedTimestamp());
      notificationViews.add(withdrawalApprovalNotificationView);
    }

    List<NotificationView> withdrawalRequestNotificationViews = ApplicationUtil.getOpenWithdrawalRequests(appData).stream()
        .map(this::getWithdrawalRequestNotificationView)
        .collect(Collectors.toList());
    notificationViews.addAll(withdrawalRequestNotificationViews);

    List<NotificationView> withdrawalRejectionNotificationViews = appData.getWithdrawalRejections().stream()
        .map(this::getWithdrawalRejectionNotificationView)
        .collect(Collectors.toList());
    notificationViews.addAll(withdrawalRejectionNotificationViews);

    return notificationViews;
  }

  private List<StatusUpdate> getStatusUpdates(String appId, List<StatusUpdate> statusUpdates) {
    Map<StatusType, StatusUpdate> statusUpdateMap = new EnumMap<>(StatusType.class);
    statusUpdates.forEach(su -> statusUpdateMap.put(su.getStatusType(), su));
    return ApplicationUtil.getAscendingStatusTypeList().stream().map(statusType -> {
      StatusUpdate statusUpdate = statusUpdateMap.get(statusType);
      if (statusUpdate != null) {
        return statusUpdate;
      } else {
        return new StatusUpdate(statusUpdateId(), appId, statusType, null);
      }
    }).collect(Collectors.toList());
  }

  private NotificationView getStopNotificationView(Notification notification) {
    String link = LinkUtil.getStoppedMessageLink(notification);
    return new NotificationView(null, "View reason for stop", link, null, notification.getCreatedTimestamp());
  }

  private NotificationView getDelayNotificationView(Notification notification) {
    String time = TimeUtil.formatDateAndTime(notification.getCreatedTimestamp());
    String description = "on " + time;
    String link = LinkUtil.getDelayedMessageLink(notification);
    return new NotificationView(EventLabelType.DELAYED, "Apology for delay received", link, description, notification.getCreatedTimestamp());
  }

  private NotificationView getInformNotificationView(Notification notification) {
    String time = TimeUtil.formatDateAndTime(notification.getCreatedTimestamp());
    String description = "on " + time;
    String link = LinkUtil.getInformLettersLink(notification.getAppId());
    return new NotificationView(EventLabelType.INFORM_ISSUED, "Inform letter issued", link, description, notification.getCreatedTimestamp());
  }

  private NotificationView getWithdrawalRejectionNotificationView(WithdrawalRejection withdrawalRejection) {
    String time = TimeUtil.formatDateAndTime(withdrawalRejection.getCreatedTimestamp());
    String description = "on " + time;
    String link = LinkUtil.getWithdrawalRejectionMessageLink(withdrawalRejection);
    return new NotificationView(EventLabelType.WITHDRAWAL_REJECTED, "Withdrawal rejected", link, description, withdrawalRejection.getCreatedTimestamp());
  }

  private NotificationView getOutcomeNotificationView(String appId) {
    String link = LinkUtil.getOutcomeDocumentsLink(appId);
    return new NotificationView(null, "View outcome documents", link, null, System.currentTimeMillis());
  }

  private NotificationView getWithdrawalRequestNotificationView(WithdrawalRequest withdrawalRequest) {
    String time = TimeUtil.formatDateAndTime(withdrawalRequest.getCreatedTimestamp());
    String description = "sent on " + time;
    String link = LinkUtil.getWithdrawalRequestMessageLink(withdrawalRequest);
    return new NotificationView(EventLabelType.WITHDRAWAL_REQUESTED, "Withdrawal request", link, description, withdrawalRequest.getCreatedTimestamp());
  }

  private List<NotificationView> getRfiNotificationViews(AppData appData) {
    return ApplicationUtil.getOpenRfiList(appData).stream()
        .map(this::getRfiNotificationView)
        .collect(Collectors.toList());
  }

  private NotificationView getRfiNotificationView(Rfi rfi) {
    String time = TimeUtil.formatDateAndTime(rfi.getCreatedTimestamp());
    String description = "Received on " + time;
    String link = controllers.routes.RfiTabController.showRfiTab(rfi.getAppId()).withFragment(rfi.getId()).toString();
    return new NotificationView(EventLabelType.RFI, "Request for information", link, description, rfi.getCreatedTimestamp());
  }

  private StatusItemView getStatusItemView(StatusUpdate statusUpdate, List<NotificationView> notificationViews, Long finishedTimestamp) {
    String status = ApplicationUtil.getStatusName(statusUpdate.getStatusType());
    String statusExplanation = ApplicationUtil.getStatusExplanation(statusUpdate.getStatusType());
    String processingLabel = getProcessingLabel(statusUpdate, finishedTimestamp);
    String processingDescription = getProcessingDescription(statusUpdate, finishedTimestamp);
    notificationViews.sort(Comparators.NOTIFICATION_VIEW_CREATED);
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, notificationViews);
  }

  private String getProcessingLabel(StatusUpdate statusUpdate, Long finishedTimestamp) {
    if (finishedTimestamp != null) {
      return ApplicationUtil.FINISHED;
    } else if (statusUpdate.getCreatedTimestamp() == null) {
      return ApplicationUtil.NOT_STARTED;
    } else {
      return ApplicationUtil.IN_PROGRESS;
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

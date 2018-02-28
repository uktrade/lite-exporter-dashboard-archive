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
import models.AppData;
import models.Application;
import models.CaseData;
import models.Notification;
import models.Rfi;
import models.StatusUpdate;
import models.WithdrawalApproval;
import models.WithdrawalRejection;
import models.WithdrawalRequest;
import models.enums.EventLabelType;
import models.enums.StatusType;
import models.view.NotificationView;
import models.view.StatusItemView;
import models.view.StatusTrackerView;
import utils.common.ViewUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatusTrackerViewServiceImpl implements StatusTrackerViewService {

  @Inject
  public StatusTrackerViewServiceImpl() {
  }

  @Override
  public StatusTrackerView getStatusTrackerView(AppData appData) {
    StatusItemView draftStatusItemView = createDraftStatusItemView(appData.getApplication());
    StatusItemView submittedStatusItemView = createSubmittedStatusItemView(appData.getSubmittedTimestamp());
    List<StatusItemView> updateStatusItemViews = createUpdateStatusItemViews(appData);

    List<StatusItemView> originalStatusItemViews = new ArrayList<>();
    originalStatusItemViews.add(draftStatusItemView);
    originalStatusItemViews.add(submittedStatusItemView);
    originalStatusItemViews.addAll(updateStatusItemViews);

    return new StatusTrackerView(originalStatusItemViews, getCaseStatusItemViews(appData));
  }

  private List<List<StatusItemView>> getCaseStatusItemViews(AppData appData) {
    String appId = appData.getApplication().getId();
    return appData.getCaseDataList().stream()
        .sorted(Comparators.CASE_DATA_CREATED.reversed())
        .map(caseData -> {
          List<NotificationView> notificationViews = new ArrayList<>();
          List<Rfi> openRfiList = ApplicationUtil.getOpenRfiList(caseData);
          notificationViews.addAll(getRfiNotificationViews(appId, openRfiList));
          notificationViews.addAll(getInformNotificationViews(appId, caseData.getInformNotifications()));
          if (caseData.getStopNotification() != null) {
            notificationViews.add(getStopNotificationView(appId, caseData.getStopNotification()));
          }
          notificationViews.sort(Comparators.NOTIFICATION_VIEW_CREATED);
          List<StatusItemView> statusItemViews = new ArrayList<>();
          StatusItemView amendmentStatusItemView = new StatusItemView(ApplicationUtil.AMENDMENT,
              "",
              getProcessingLabel(caseData),
              getProcessingDescription(caseData),
              notificationViews);
          statusItemViews.add(amendmentStatusItemView);

          if (caseData.getOutcome() != null) {
            statusItemViews.add(getOutcomeStatusItemView(appData.getApplication().getId(), caseData));
          }

          return statusItemViews;
        })
        .collect(Collectors.toList());
  }

  private StatusItemView getOutcomeStatusItemView(String appId, CaseData caseData) {
    String processDescription = "Completed on " + TimeUtil.formatDate(caseData.getOutcome().getCreatedTimestamp());
    return new StatusItemView(ApplicationUtil.OUTCOME_DOCUMENTS_UPDATED,
        "",
        ApplicationUtil.FINISHED,
        processDescription,
        Collections.singletonList(getOutcomeNotificationView(appId)));
  }

  private StatusItemView createDraftStatusItemView(Application application) {
    String status = ApplicationUtil.DRAFT;
    String statusExplanation = "";
    String processingLabel = ApplicationUtil.FINISHED;
    String processingDescription = "Created on " + TimeUtil.formatDate(application.getCreatedTimestamp());
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, new ArrayList<>());
  }

  private StatusItemView createSubmittedStatusItemView(Long submittedTimestamp) {
    String status = ApplicationUtil.SUBMITTED;
    String statusExplanation = "";
    String processingLabel;
    String processingDescription;
    if (submittedTimestamp == null) {
      processingLabel = ApplicationUtil.NOT_STARTED;
      processingDescription = "";
    } else {
      processingLabel = ApplicationUtil.FINISHED;
      processingDescription = "Submitted on " + TimeUtil.formatDate(submittedTimestamp);
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

    if (appData.getWithdrawalApproval() != null || appData.getStopNotification() != null) {
      int j = 0;
      for (int i = statusUpdates.size() - 1; i >= 0; i--) {
        StatusUpdate statusUpdate = statusUpdates.get(i);
        if (statusUpdate.getCreatedTimestamp() != null && finishedTimestamps.get(statusUpdate) == null) {
          j = i;
          break;
        }
      }
      StatusItemView inProgressStatusItemView = statusItemViews.get(j);
      if (appData.getWithdrawalApproval() != null) {
        StatusItemView withdrawalApprovalStatusItemView = getWithdrawalApprovalStatusItemView(appData.getWithdrawalApproval(), inProgressStatusItemView);
        statusItemViews.set(j, withdrawalApprovalStatusItemView);
      } else {
        StatusItemView stopStatusItemView = geStopStatusItemView(appData.getStopNotification(), inProgressStatusItemView);
        statusItemViews.set(j, stopStatusItemView);
      }
    }

    return statusItemViews;
  }

  private List<NotificationView> getNotificationViews(AppData appData) {
    String appId = appData.getApplication().getId();
    List<NotificationView> notificationViews = new ArrayList<>();

    List<Rfi> openRfiList = ApplicationUtil.getOpenRfiList(appData);
    notificationViews.addAll(getRfiNotificationViews(appId, openRfiList));

    notificationViews.addAll(getWithdrawalNotificationViews(appData));

    if (appData.getDelayNotification() != null) {
      notificationViews.add(getDelayNotificationView(appId, appData.getDelayNotification()));
    }

    notificationViews.addAll(getInformNotificationViews(appId, appData.getInformNotifications()));

    if (appData.getStopNotification() != null) {
      notificationViews.add(getStopNotificationView(appId, appData.getStopNotification()));
    }

    if (appData.getOutcome() != null) {
      notificationViews.add(getOutcomeNotificationView(appId));
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
    return ApplicationUtil.getAscendingStatusTypeList().stream()
        .map(statusType -> statusUpdateMap.getOrDefault(statusType,
            new StatusUpdate(statusUpdateId(), appId, statusType, null)))
        .collect(Collectors.toList());
  }

  private NotificationView getStopNotificationView(String appId, Notification notification) {
    String link = LinkUtil.getStoppedMessageLink(appId, notification);
    return new NotificationView(null, "View reason for stop", link, null, notification.getCreatedTimestamp());
  }

  private NotificationView getDelayNotificationView(String appId, Notification notification) {
    String time = TimeUtil.formatDateAndTime(notification.getCreatedTimestamp());
    String description = "on " + time;
    String link = LinkUtil.getDelayedMessageLink(appId, notification);
    return new NotificationView(EventLabelType.DELAYED, "Apology for delay received", link, description, notification.getCreatedTimestamp());
  }

  private List<NotificationView> getInformNotificationViews(String appId, List<Notification> notifications) {
    return notifications.stream()
        .map(notification -> getInformNotificationView(appId, notification))
        .collect(Collectors.toList());
  }

  private NotificationView getInformNotificationView(String appId, Notification notification) {
    String time = TimeUtil.formatDateAndTime(notification.getCreatedTimestamp());
    String description = "on " + time;
    String link = LinkUtil.getInformLettersLink(appId);
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

  private List<NotificationView> getRfiNotificationViews(String appId, List<Rfi> openRfiList) {
    return openRfiList.stream()
        .map(rfi -> getRfiNotificationView(appId, rfi))
        .collect(Collectors.toList());
  }

  private NotificationView getRfiNotificationView(String appId, Rfi rfi) {
    String time = TimeUtil.formatDateAndTime(rfi.getCreatedTimestamp());
    String description = "received on " + time;
    String link = controllers.routes.RfiTabController.showRfiTab(appId).withFragment(rfi.getId()).toString();
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

  private String getProcessingLabel(CaseData caseData) {
    if (caseData.getOutcome() != null) {
      return ApplicationUtil.FINISHED;
    } else if (caseData.getStopNotification() != null) {
      return ApplicationUtil.STOPPED;
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
          return "Processed in " + ViewUtil.pluraliseWithCount(duration, "working day") + "*";
        } else {
          String started = TimeUtil.formatDate(createdTimestamp);
          long duration = TimeUtil.daysBetweenWithStartBeforeEnd(createdTimestamp, Instant.now().toEpochMilli());
          return String.format("Started on %s<br>(%s* ago)", started, ViewUtil.pluraliseWithCount(duration, "working day"));
        }
      } else {
        return "";
      }
    }
  }

  private String getProcessingDescription(CaseData caseData) {
    if (caseData.getOutcome() != null) {
      return "Started on " + TimeUtil.formatDate(caseData.getCaseDetails().getCreatedTimestamp());
    } else if (caseData.getStopNotification() != null) {
      return "On " + TimeUtil.formatDate(caseData.getStopNotification().getCreatedTimestamp());
    } else {
      long createdTimestamp = caseData.getCaseDetails().getCreatedTimestamp();
      String started = TimeUtil.formatDate(createdTimestamp);
      long duration = TimeUtil.daysBetweenWithStartBeforeEnd(createdTimestamp, Instant.now().toEpochMilli());
      return String.format("Started on %s<br>(%d days ago)", started, duration);
    }
  }

}

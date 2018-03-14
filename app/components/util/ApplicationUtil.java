package components.util;

import models.AppData;
import models.CaseData;
import models.Notification;
import models.Outcome;
import models.Rfi;
import models.RfiReply;
import models.RfiWithdrawal;
import models.StatusColumnInfo;
import models.StatusUpdate;
import models.WithdrawalRejection;
import models.WithdrawalRequest;
import models.enums.StatusType;
import org.apache.commons.collections4.CollectionUtils;
import uk.gov.bis.lite.permissions.api.view.LicenceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationUtil {

  public static final String CREATED = "Created";

  public static final String RE_OPENED = "Re-opened";

  public static final String COMPLETED = "Completed";

  public static final String WITHDRAWN = "Withdrawn";

  public static final String STOPPED = "Stopped";

  public static final String SUBMITTED = "Submitted";

  public static final String DRAFT = "Draft";

  public static final String FINISHED = "Finished";

  public static final String NOT_STARTED = "Not started";

  public static final String IN_PROGRESS = "In progress";

  public static final String AMENDMENT = "Amendment";

  public static final String OUTCOME_DOCUMENTS_UPDATED = "Outcome documents updated";

  public static final String BEING_AMENDED = "Being amended";

  public static final String AMENDMENT_STOPPED = "Amendment stopped";

  private static final Map<StatusType, String> STATUS_NAME_MAP;

  private static final Map<StatusType, String> STATUS_EXPLANATION_MAP;

  private final static List<StatusType> STATUS_TYPE_LIST;

  private static final Comparator<StatusUpdate> STATUS_UPDATE_COMPARATOR;

  static {
    Map<StatusType, String> statuses = new EnumMap<>(StatusType.class);
    statuses.put(StatusType.INITIAL_CHECKS, "Initial Checks");
    statuses.put(StatusType.TECHNICAL_ASSESSMENT, "Technical assessment");
    statuses.put(StatusType.LU_PROCESSING, "Licensing unit processing");
    statuses.put(StatusType.WITH_OGD, "With OGD advisers");
    statuses.put(StatusType.FINAL_ASSESSMENT, "Final assessment");
    statuses.put(StatusType.COMPLETE, "Decision reached");
    STATUS_NAME_MAP = Collections.unmodifiableMap(statuses);

    Map<StatusType, String> statusExplanations = new EnumMap<>(StatusType.class);
    statusExplanations.put(StatusType.INITIAL_CHECKS, "Checking your organisation details");
    statusExplanations.put(StatusType.TECHNICAL_ASSESSMENT, "Classification code validation");
    statusExplanations.put(StatusType.LU_PROCESSING, "Checking application form details and other documentation");
    statusExplanations.put(StatusType.WITH_OGD, "Additional advice from other government departments");
    statusExplanations.put(StatusType.FINAL_ASSESSMENT, "Your application is undergoing final checks and you will receive an outcome soon");
    STATUS_EXPLANATION_MAP = Collections.unmodifiableMap(statusExplanations);

    List<StatusType> statusTypeList = Arrays.asList(
        StatusType.INITIAL_CHECKS,
        StatusType.TECHNICAL_ASSESSMENT,
        StatusType.LU_PROCESSING,
        StatusType.WITH_OGD,
        StatusType.FINAL_ASSESSMENT,
        StatusType.COMPLETE);
    STATUS_TYPE_LIST = Collections.unmodifiableList(statusTypeList);

    STATUS_UPDATE_COMPARATOR = Comparator.comparing(statusUpdate -> STATUS_TYPE_LIST.indexOf(statusUpdate.getStatusType()));
  }

  public static String getStatusName(StatusType statusType) {
    return STATUS_NAME_MAP.get(statusType);
  }

  public static String getStatusExplanation(StatusType statusType) {
    return STATUS_EXPLANATION_MAP.get(statusType);
  }

  public static List<StatusType> getAscendingStatusTypeList() {
    return STATUS_TYPE_LIST;
  }

  public static StatusUpdate getMaxStatusUpdate(Collection<StatusUpdate> statusUpdates) {
    if (!CollectionUtils.isEmpty(statusUpdates)) {
      return Collections.max(statusUpdates, STATUS_UPDATE_COMPARATOR);
    } else {
      return null;
    }
  }

  public static String getSielDestinations(LicenceView licenceView) {
    int destinationCount = licenceView.getCountryList().size();
    if (destinationCount == 0) {
      return "";
    } else if (destinationCount == 1) {
      return licenceView.getCountryList().get(0);
    } else {
      return String.format("%d destinations", destinationCount);
    }
  }

  public static boolean isOriginalApplicationInProgress(AppData appData) {
    if (!appData.getCaseDataList().isEmpty()) {
      return false;
    } else {
      StatusUpdate maxStatusUpdate = getMaxStatusUpdate(appData.getStatusUpdates());
      boolean isStopped = appData.getStopNotification() != null;
      boolean isWithdrawn = appData.getWithdrawalApproval() != null;
      boolean isComplete = maxStatusUpdate != null && maxStatusUpdate.getStatusType() == StatusType.COMPLETE;
      return !isStopped && !isWithdrawn && !isComplete;
    }
  }

  public static boolean isCaseInProgress(CaseData caseData) {
    return isCaseStarted(caseData) && !isCaseFinished(caseData);
  }

  public static boolean isCaseStarted(CaseData caseData) {
    return !caseData.getRfiList().isEmpty() || !caseData.getInformNotifications().isEmpty();
  }

  public static boolean isCaseFinished(CaseData caseData) {
    return caseData.getOutcome() != null || caseData.getStopNotification() != null;
  }

  public static CaseData getMostRecentCase(AppData appData) {
    return appData.getCaseDataList().stream()
        .sorted(Comparators.CASE_DATA_CREATED.reversed())
        .findFirst()
        .orElse(null);
  }

  public static StatusColumnInfo getStatusInfo(AppData appData) {
    if (!appData.getCaseDataList().isEmpty()) {
      CaseData caseData = ApplicationUtil.getMostRecentCase(appData);
      if (caseData.getOutcome() != null) {
        return new StatusColumnInfo("On", caseData.getOutcome().getCreatedTimestamp(), OUTCOME_DOCUMENTS_UPDATED);
      } else if (caseData.getStopNotification() != null) {
        return new StatusColumnInfo("On", caseData.getStopNotification().getCreatedTimestamp(), AMENDMENT_STOPPED);
      } else {
        return new StatusColumnInfo("Since", caseData.getCaseDetails().getCreatedTimestamp(), BEING_AMENDED);
      }
    } else {
      StatusUpdate maxStatusUpdate = getMaxStatusUpdate(appData.getStatusUpdates());
      if (appData.getWithdrawalApproval() != null) {
        return new StatusColumnInfo("On", appData.getWithdrawalApproval().getCreatedTimestamp(), WITHDRAWN);
      } else if (appData.getStopNotification() != null) {
        return new StatusColumnInfo("On", appData.getStopNotification().getCreatedTimestamp(), STOPPED);
      } else if (maxStatusUpdate != null) {
        if (maxStatusUpdate.getStatusType() == StatusType.COMPLETE) {
          return new StatusColumnInfo("On", maxStatusUpdate.getCreatedTimestamp(), getStatusName(StatusType.COMPLETE));
        } else {
          return new StatusColumnInfo("Since", maxStatusUpdate.getCreatedTimestamp(), getStatusName(maxStatusUpdate.getStatusType()));
        }
      } else if (appData.getSubmittedTimestamp() != null) {
        return new StatusColumnInfo("On", appData.getSubmittedTimestamp(), SUBMITTED);
      } else {
        return new StatusColumnInfo("Since", appData.getApplication().getCreatedTimestamp(), DRAFT);
      }
    }
  }

  public static List<Rfi> getAllOpenRfiList(AppData appData) {
    List<Rfi> openRfiList = ApplicationUtil.getOpenRfiList(appData);
    appData.getCaseDataList().forEach(caseData -> openRfiList.addAll(getOpenRfiList(caseData)));
    return openRfiList;
  }

  public static List<Rfi> getOpenRfiList(CaseData caseData) {
    return getOpenRfiList(caseData.getRfiList(), caseData.getRfiReplies(), caseData.getRfiWithdrawals());
  }

  public static List<Rfi> getOpenRfiList(AppData appData) {
    return getOpenRfiList(appData.getRfiList(), appData.getRfiReplies(), appData.getRfiWithdrawals());
  }

  private static List<Rfi> getOpenRfiList(List<Rfi> rfiList, List<RfiReply> rfiReplies, List<RfiWithdrawal> rfiWithdrawals) {
    Set<String> repliedToRfiIds = rfiReplies.stream()
        .map(RfiReply::getRfiId)
        .collect(Collectors.toSet());
    Set<String> withdrawnRfiIds = rfiWithdrawals.stream()
        .map(RfiWithdrawal::getRfiId)
        .collect(Collectors.toSet());
    return rfiList.stream()
        .filter(rfi -> !repliedToRfiIds.contains(rfi.getId()) && !withdrawnRfiIds.contains(rfi.getId()))
        .collect(Collectors.toList());
  }

  public static List<WithdrawalRequest> getOpenWithdrawalRequests(AppData appData) {
    List<WithdrawalRequest> withdrawalRequests = new ArrayList<>(appData.getWithdrawalRequests());
    withdrawalRequests.sort(Comparators.WITHDRAWAL_REQUEST_CREATED);
    appData.getWithdrawalRejections().forEach(withdrawalRejection -> withdrawalRequests.remove(0));
    if (appData.getWithdrawalApproval() != null) {
      withdrawalRequests.remove(withdrawalRequests.size() - 1);
    }
    return withdrawalRequests;
  }

  public static Map<String, WithdrawalRejection> getWithdrawalRejectionMap(AppData appData) {
    Map<String, WithdrawalRejection> withdrawalRejectionMap = new HashMap<>();
    List<WithdrawalRequest> withdrawalRequests = new ArrayList<>(appData.getWithdrawalRequests());
    List<WithdrawalRejection> withdrawalRejections = new ArrayList<>(appData.getWithdrawalRejections());
    withdrawalRequests.sort(Comparators.WITHDRAWAL_REQUEST_CREATED);
    withdrawalRejections.sort(Comparators.WITHDRAWAL_REJECTION_CREATED);
    for (int i = 0; i < Math.min(withdrawalRequests.size(), withdrawalRejections.size()); i++) {
      withdrawalRejectionMap.put(withdrawalRequests.get(i).getId(), withdrawalRejections.get(i));
    }
    return withdrawalRejectionMap;
  }

  public static WithdrawalRequest getApprovedWithdrawalRequest(AppData appData) {
    if (appData.getWithdrawalApproval() != null) {
      return appData.getWithdrawalRequests().stream()
          .sorted(Comparators.WITHDRAWAL_REQUEST_CREATED_REVERSED)
          .findFirst()
          .orElse(null);
    } else {
      return null;
    }
  }

  public static boolean hasPendingWithdrawalRequest(AppData appData) {
    return appData.getWithdrawalApproval() == null && appData.getWithdrawalRequests().size() > appData.getWithdrawalRejections().size();
  }

  public static List<Rfi> getAllRfi(AppData appData) {
    List<Rfi> rfiList = new ArrayList<>(appData.getRfiList());
    appData.getCaseDataList().forEach(caseData -> rfiList.addAll(caseData.getRfiList()));
    return rfiList;
  }

  public static List<RfiReply> getAllRfiReplies(AppData appData) {
    List<RfiReply> rfiReplies = new ArrayList<>(appData.getRfiReplies());
    appData.getCaseDataList().forEach(caseData -> rfiReplies.addAll(caseData.getRfiReplies()));
    return rfiReplies;
  }

  public static List<RfiWithdrawal> getAllRfiWithdrawals(AppData appData) {
    List<RfiWithdrawal> rfiWithdrawals = new ArrayList<>(appData.getRfiWithdrawals());
    appData.getCaseDataList().forEach(caseData -> rfiWithdrawals.addAll(caseData.getRfiWithdrawals()));
    return rfiWithdrawals;
  }

  public static List<Outcome> getAllOutcomes(AppData appData) {
    List<Outcome> outcomes = new ArrayList<>();
    CollectionUtils.addIgnoreNull(outcomes, appData.getOutcome());
    appData.getCaseDataList().forEach(caseData -> CollectionUtils.addIgnoreNull(outcomes, caseData.getOutcome()));
    return outcomes;
  }

  public static List<Notification> getAllStopNotifications(AppData appData) {
    List<Notification> notifications = new ArrayList<>();
    CollectionUtils.addIgnoreNull(notifications, appData.getStopNotification());
    appData.getCaseDataList().forEach(caseData -> CollectionUtils.addIgnoreNull(notifications, caseData.getStopNotification()));
    return notifications;
  }

  public static List<Notification> getAllInformNotifications(AppData appData) {
    List<Notification> notifications = new ArrayList<>(appData.getInformNotifications());
    appData.getCaseDataList().forEach(caseData -> notifications.addAll(caseData.getInformNotifications()));
    return notifications;
  }

  public static Optional<Rfi> getRfi(List<Rfi> rfiList, String rfiId) {
    return rfiList.stream()
        .filter(rfi -> rfi.getId().equals(rfiId))
        .findAny();
  }

}

package components.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import models.AppData;
import models.Rfi;
import models.RfiWithdrawal;
import models.StatusUpdate;
import models.enums.StatusType;
import org.apache.commons.collections.CollectionUtils;
import models.RfiReply;
import models.WithdrawalRequest;

public class ApplicationUtil {

  public static final String WITHDRAWN = "Withdrawn";

  public static final String STOPPED = "Stopped";

  public static final String SUBMITTED = "Submitted";

  public static final String DRAFT = "Draft";

  public static final String FINISHED = "Finished";

  public static final String NOT_STARTED = "Not started";

  public static final String IN_PROGRESS = "In progress";

  private static final Map<StatusType, String> STATUS_NAME_MAP;

  private static final Map<StatusType, String> STATUS_EXPLANATION_MAP;

  private final static List<StatusType> STATUS_TYPE_LIST;

  private static final List<StatusType> INVERSE_STATUS_TYPE_LIST;

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
    INVERSE_STATUS_TYPE_LIST = Collections.unmodifiableList(Lists.reverse(statusTypeList));
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
      Map<StatusType, StatusUpdate> statusUpdateMap = new EnumMap<>(StatusType.class);
      statusUpdates.forEach(statusUpdate -> statusUpdateMap.put(statusUpdate.getStatusType(), statusUpdate));
      for (StatusType statusType : INVERSE_STATUS_TYPE_LIST) {
        StatusUpdate statusUpdate = statusUpdateMap.get(statusType);
        if (statusUpdate != null) {
          return statusUpdate;
        }
      }
    }
    return null;
  }

  public static String getDestinations(List<String> destinationList) {
    int destinationCount = destinationList.size();
    if (destinationCount == 1) {
      return destinationList.get(0);
    } else if (destinationCount > 1) {
      return String.format("%d destinations", destinationCount);
    } else {
      return "";
    }
  }

  public static boolean isApplicationInProgress(AppData appData) {
    StatusUpdate maxStatusUpdate = getMaxStatusUpdate(appData.getStatusUpdates());
    boolean isStopped = appData.getStopNotification() != null;
    boolean isWithdrawn = appData.getWithdrawalApproval() != null;
    boolean isComplete = maxStatusUpdate != null && maxStatusUpdate.getStatusType() == StatusType.COMPLETE;
    return !isStopped && !isWithdrawn && !isComplete;
  }

  public static String getApplicationStatus(AppData appData) {
    StatusUpdate maxStatusUpdate = getMaxStatusUpdate(appData.getStatusUpdates());
    if (appData.getWithdrawalApproval() != null) {
      return ApplicationUtil.WITHDRAWN;
    } else if (appData.getStopNotification() != null) {
      return ApplicationUtil.STOPPED;
    } else if (maxStatusUpdate != null) {
      return ApplicationUtil.getStatusName(maxStatusUpdate.getStatusType());
    } else if (appData.getApplication().getSubmittedTimestamp() != null) {
      return ApplicationUtil.SUBMITTED;
    } else {
      return ApplicationUtil.DRAFT;
    }
  }

  public static List<Rfi> getOpenRfiList(AppData appData) {
    Set<String> repliedToRfiIds = appData.getRfiReplies().stream()
        .map(RfiReply::getRfiId)
        .collect(Collectors.toSet());
    Set<String> withdrawnRfiIds = appData.getRfiWithdrawals().stream()
        .map(RfiWithdrawal::getRfiId)
        .collect(Collectors.toSet());
    return appData.getRfiList().stream()
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

}

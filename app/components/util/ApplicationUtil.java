package components.util;

import com.google.common.collect.Lists;
import models.Application;
import models.StatusUpdate;
import models.enums.StatusType;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ApplicationUtil {

  public static final String SUBMITTED = "Submitted";

  public static final String DRAFT = "Draft";

  private static final Map<StatusType, String> STATUS_NAME_MAP;

  private static final Map<StatusType, String> STATUS_EXPLANATION_MAP;

  private final static List<StatusType> STATUS_TYPE_LIST;

  private static final List<StatusType> INVERSE_STATUS_TYPE_LIST;

  static {
    Map<StatusType, String> statuses = new EnumMap<>(StatusType.class);
    statuses.put(StatusType.INITIAL_CHECKS, "Initial Checks");
    statuses.put(StatusType.TECHNICAL_ASSESSMENT, "Technical assessment");
    statuses.put(StatusType.LU_PROCESSING, "Licensing unit processing");
    statuses.put(StatusType.WITH_OGD, "With OGD");
    statuses.put(StatusType.FINAL_ASSESSMENT, "Final assessment");
    statuses.put(StatusType.COMPLETE, "Decision reached");
    STATUS_NAME_MAP = Collections.unmodifiableMap(statuses);

    Map<StatusType, String> statusExplanations = new EnumMap<>(StatusType.class);
    statusExplanations.put(StatusType.INITIAL_CHECKS, "Checking your organisation details");
    statusExplanations.put(StatusType.TECHNICAL_ASSESSMENT, "Technical assessment");
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

  public static String getDestination(Application application) {
    int destinationCount = application.getDestinationList().size();
    if (destinationCount == 1) {
      return application.getDestinationList().get(0);
    } else if (destinationCount > 1) {
      return String.format("%d destinations", destinationCount);
    } else {
      return "";
    }
  }

}

package components.service;

import models.enums.StatusType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StatusServiceImpl implements StatusService {

  private static final Map<StatusType, String> statusMap;

  static {
    Map<StatusType, String> statuses = new HashMap<>();
    statuses.put(StatusType.DRAFT, "Draft");
    statuses.put(StatusType.SUBMITTED, "Submitted");
    statuses.put(StatusType.INITIAL_CHECKS, "Initial Checks");
    statuses.put(StatusType.TECHNICAL_ASSESSMENT, "Technical assessment");
    statuses.put(StatusType.LU_PROCESSING, "Licensing unit processing");
    statuses.put(StatusType.WITH_OGD, "With OGD");
    statuses.put(StatusType.FINAL_ASSESSMENT, "Final assessment");
    statuses.put(StatusType.COMPLETE, "Decision reached");
    statusMap = Collections.unmodifiableMap(statuses);
  }

  @Override
  public String getStatus(StatusType statusType) {
    return statusMap.get(statusType);
  }

}

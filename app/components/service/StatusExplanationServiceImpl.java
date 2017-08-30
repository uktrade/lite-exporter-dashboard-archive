package components.service;

import models.enums.StatusType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StatusExplanationServiceImpl implements StatusExplanationService {

  private static final Map<StatusType, String> statusExplanationMap;

  static {
    Map<StatusType, String> statusExplanations = new HashMap<>();
    statusExplanations.put(StatusType.INITIAL_CHECKS, "Checking your organisation details");
    statusExplanations.put(StatusType.TECHNICAL_ASSESSMENT, "Technical assessment");
    statusExplanations.put(StatusType.LU_PROCESSING, "Checking application form details and other documentation");
    statusExplanations.put(StatusType.WITH_OGD, "Additional advice from other government departments");
    statusExplanations.put(StatusType.FINAL_ASSESSMENT, "Your application is undergoing final checks and you will receive an outcome soon");
    statusExplanationMap = Collections.unmodifiableMap(statusExplanations);
  }

  @Override
  public String getStatusExplanation(StatusType statusType) {
    return statusExplanationMap.get(statusType);
  }

  @Override
  public String getDraftStatusExplanation() {
    return "";
  }

  @Override
  public String getSubmittedStatusExplanation() {
    return "";
  }

}

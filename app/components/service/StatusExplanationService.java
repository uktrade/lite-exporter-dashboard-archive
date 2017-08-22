package components.service;

import models.enums.StatusType;

public interface StatusExplanationService {

  String getStatusExplanation(StatusType statusType);

  String getDraftStatusExplanation();

  String getSubmittedStatusExplanation();
}

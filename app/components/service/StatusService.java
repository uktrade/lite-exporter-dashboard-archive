package components.service;

import models.enums.StatusType;

public interface StatusService {

  String getStatus(StatusType statusType);

  String getSubmitted();

  String getDraft();
}

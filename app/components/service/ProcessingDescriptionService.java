package components.service;

import models.Application;
import models.StatusUpdate;

public interface ProcessingDescriptionService {

  String getProcessingDescription(StatusUpdate statusUpdate);

  String getDraftProcessingDescription(Application application);

  String getSubmittedProcessingDescription(Application application);
}

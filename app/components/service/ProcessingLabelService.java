package components.service;

import models.Application;
import models.StatusUpdate;

public interface ProcessingLabelService {

  String getProcessingLabel(StatusUpdate statusUpdate);

  String getDraftProcessingLabel();

  String getSubmittedProcessingLabel(Application application);
}

package components.service;

import models.Application;
import models.StatusUpdate;
import models.enums.StatusType;

public class ProcessingLabelServiceImpl implements ProcessingLabelService {

  @Override
  public String getProcessingLabel(StatusUpdate statusUpdate) {
    if (statusUpdate.getStatusType() == StatusType.COMPLETE && statusUpdate.getStartTimestamp() != null || statusUpdate.getEndTimestamp() != null) {
      return "Finished";
    } else if (statusUpdate.getStartTimestamp() == null && statusUpdate.getEndTimestamp() == null) {
      return "Not started";
    } else if (statusUpdate.getEndTimestamp() != null) {
      return "Finished";
    } else {
      return "In progress";
    }
  }

  @Override
  public String getDraftProcessingLabel() {
    return "Finished";
  }

  @Override
  public String getSubmittedProcessingLabel(Application application) {
    if (application.getSubmittedTimestamp() == null) {
      return "Not started";
    } else {
      return "Finished";
    }
  }

}

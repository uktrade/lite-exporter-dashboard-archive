package components.service;

import models.StatusUpdate;

public class ProcessingLabelServiceImpl implements ProcessingLabelService {

  @Override
  public String getProcessingLabel(StatusUpdate statusUpdate) {
    switch (statusUpdate.getStatusType()) {
      case DRAFT:
      case SUBMITTED:
        if (statusUpdate.getStartTimestamp() != null) {
          return "Finished";
        } else {
          return "Not started";
        }
      case INITIAL_CHECKS:
      case TECHNICAL_ASSESSMENT:
      case LU_PROCESSING:
      case WITH_OGD:
      case FINAL_ASSESSMENT:
      case COMPLETE:
        if (statusUpdate.getStartTimestamp() == null && statusUpdate.getEndTimestamp() == null) {
          return "Not started";
        } else if (statusUpdate.getEndTimestamp() != null) {
          return "Finished";
        } else {
          return "In progress";
        }
      default:
        return "";
    }
  }

}

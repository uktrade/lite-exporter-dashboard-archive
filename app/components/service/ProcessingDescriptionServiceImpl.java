package components.service;

import com.google.inject.Inject;
import models.StatusUpdate;

import java.time.Instant;

public class ProcessingDescriptionServiceImpl implements ProcessingDescriptionService {

  private final TimeFormatService timeFormatService;
  private final WorkingDaysCalculatorService workingDaysCalculatorService;

  @Inject
  public ProcessingDescriptionServiceImpl(TimeFormatService timeFormatService, WorkingDaysCalculatorService workingDaysCalculatorService) {
    this.timeFormatService = timeFormatService;
    this.workingDaysCalculatorService = workingDaysCalculatorService;
  }

  @Override
  public String getProcessingDescription(StatusUpdate statusUpdate) {
    Long startTimestamp = statusUpdate.getStartTimestamp();
    Long endTimestamp = statusUpdate.getEndTimestamp();
    switch (statusUpdate.getStatusType()) {
      case DRAFT:
        return "Created on " + timeFormatService.formatDateAndTime(startTimestamp);
      case SUBMITTED:
        if (startTimestamp != null) {
          return "Submitted on " + timeFormatService.formatDateAndTime(startTimestamp);
        } else {
          return "";
        }
      case INITIAL_CHECKS:
      case TECHNICAL_ASSESSMENT:
      case LU_PROCESSING:
      case WITH_OGD:
      case FINAL_ASSESSMENT:
      case COMPLETE:
        if (startTimestamp != null) {
          if (endTimestamp != null) {
            long duration = workingDaysCalculatorService.calculateWithStartBeforeEnd(startTimestamp, endTimestamp);
            return "Processed in " + duration + " working days";
          } else {
            String started = timeFormatService.formatDateAndTime(startTimestamp);
            long duration = workingDaysCalculatorService.calculateWithStartBeforeEnd(startTimestamp, Instant.now().toEpochMilli());
            return String.format("Started on %s<br>(%d days ago)", started, duration);
          }
        }
      default:
        return "";
    }
  }

}

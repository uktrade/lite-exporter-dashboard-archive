package components.service;

import com.google.inject.Inject;
import models.Application;
import models.StatusUpdate;
import models.enums.StatusType;

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
    if (statusUpdate.getStatusType() == StatusType.COMPLETE) {
      return "";
    } else {
      return getNonCompleteProcessingDescription(statusUpdate);
    }
  }

  private String getNonCompleteProcessingDescription(StatusUpdate statusUpdate) {
    Long startTimestamp = statusUpdate.getStartTimestamp();
    Long endTimestamp = statusUpdate.getEndTimestamp();
    if (startTimestamp != null) {
      if (endTimestamp != null) {
        long duration = workingDaysCalculatorService.calculateWithStartBeforeEnd(startTimestamp, endTimestamp);
        return "Processed in " + duration + " working days";
      } else {
        String started = timeFormatService.formatDateAndTime(startTimestamp);
        long duration = workingDaysCalculatorService.calculateWithStartBeforeEnd(startTimestamp, Instant.now().toEpochMilli());
        return String.format("Started on %s<br>(%d days ago)", started, duration);
      }
    } else {
      return "";
    }
  }

  @Override
  public String getDraftProcessingDescription(Application application) {
    return "Created on " + timeFormatService.formatDateAndTime(application.getCreatedTimestamp());
  }

  @Override
  public String getSubmittedProcessingDescription(Application application) {
    if (application.getSubmittedTimestamp() != null) {
      return "Submitted on " + timeFormatService.formatDateAndTime(application.getSubmittedTimestamp());
    } else {
      return "";
    }
  }

}

package components.service;

import com.google.inject.Inject;
import models.StatusUpdate;
import models.view.StatusItemView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StatusItemViewServiceImpl implements StatusItemViewService {

  private final TimeFormatService timeFormatService;
  private final ProcessingDescriptionService processingDescriptionService;
  private final StatusExplanationService statusExplanationService;
  private final ProcessingLabelService processingLabelService;
  private final StatusService statusService;

  @Inject
  public StatusItemViewServiceImpl(TimeFormatService timeFormatService,
                                   ProcessingDescriptionService processingDescriptionService,
                                   StatusExplanationService statusExplanationService,
                                   ProcessingLabelService processingLabelService,
                                   StatusService statusService) {
    this.timeFormatService = timeFormatService;
    this.processingDescriptionService = processingDescriptionService;
    this.statusExplanationService = statusExplanationService;
    this.processingLabelService = processingLabelService;
    this.statusService = statusService;
  }

  @Override
  public List<StatusItemView> getStatusItemViews(List<StatusUpdate> statusUpdates) {
    return statusUpdates.stream()
        .map(this::getStatusItemView)
        .collect(Collectors.toList());
  }

  private StatusItemView getStatusItemView(StatusUpdate statusUpdate) {
    String status = statusService.getStatus(statusUpdate.getStatusType());
    String statusExplanation = statusExplanationService.getStatusExplanation(statusUpdate.getStatusType());
    String processingLabel = processingLabelService.getProcessingLabel(statusUpdate);
    String processingDescription = processingDescriptionService.getProcessingDescription(statusUpdate);
    List<String> rfiList = getRfiList(processingLabel, statusUpdate);
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, rfiList);
  }

  // TODO replace with proper list from database
  private List<String> getRfiList(String processingLabel, StatusUpdate statusUpdate) {
    if ("In progress".equals(processingLabel)) {
      return Collections.singletonList("Received on " + timeFormatService.format(statusUpdate.getStartTimestamp()));
    } else {
      return new ArrayList<>();
    }
  }

}

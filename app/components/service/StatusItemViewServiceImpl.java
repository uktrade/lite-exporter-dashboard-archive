package components.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.dao.RfiDao;
import components.dao.StatusUpdateDao;
import models.Rfi;
import models.StatusUpdate;
import models.view.StatusItemRfiView;
import models.view.StatusItemView;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatusItemViewServiceImpl implements StatusItemViewService {

  private final StatusUpdateDao statusUpdateDao;
  private final RfiDao rfiDao;
  private final TimeFormatService timeFormatService;
  private final ProcessingDescriptionService processingDescriptionService;
  private final StatusExplanationService statusExplanationService;
  private final ProcessingLabelService processingLabelService;
  private final StatusService statusService;

  @Inject
  public StatusItemViewServiceImpl(StatusUpdateDao statusUpdateDao, RfiDao rfiDao, TimeFormatService timeFormatService,
                                   ProcessingDescriptionService processingDescriptionService,
                                   StatusExplanationService statusExplanationService,
                                   ProcessingLabelService processingLabelService,
                                   StatusService statusService) {
    this.statusUpdateDao = statusUpdateDao;
    this.rfiDao = rfiDao;
    this.timeFormatService = timeFormatService;
    this.processingDescriptionService = processingDescriptionService;
    this.statusExplanationService = statusExplanationService;
    this.processingLabelService = processingLabelService;
    this.statusService = statusService;
  }

  @Override
  public List<StatusItemView> getStatusItemViews(String appId) {
    List<StatusUpdate> statusUpdates = statusUpdateDao.getStatusUpdates(appId);
    List<Rfi> rfiList = rfiDao.getRfiList(appId);
    Multimap<StatusUpdate, Rfi> rfiMultimap = createRfiMultimap(statusUpdates, rfiList);
    return statusUpdates.stream()
        .map(statusUpdate -> getStatusItemView(statusUpdate, rfiMultimap.get(statusUpdate)))
        .collect(Collectors.toList());
  }

  private Multimap<StatusUpdate, Rfi> createRfiMultimap(List<StatusUpdate> statusUpdates, List<Rfi> rfiList) {
    List<StatusUpdate> inverseSortedStatusUpdates = statusUpdates.stream()
        .filter(statusUpdate -> statusUpdate.getStartTimestamp() != null)
        .sorted(Comparator.comparing(StatusUpdate::getStartTimestamp).reversed())
        .collect(Collectors.toList());
    List<Rfi> sortedRfiList = rfiList.stream().
        sorted(Comparator.comparing(Rfi::getReceivedTimestamp))
        .collect(Collectors.toList());
    Multimap<StatusUpdate, Rfi> rfiMap = HashMultimap.create();
    for (Rfi rfi : sortedRfiList) {
      for (StatusUpdate statusUpdate : inverseSortedStatusUpdates) {
        if (rfi.getReceivedTimestamp() >= statusUpdate.getStartTimestamp()) {
          rfiMap.put(statusUpdate, rfi);
          break;
        }
      }
    }
    return rfiMap;
  }

  private StatusItemView getStatusItemView(StatusUpdate statusUpdate, Collection<Rfi> rfiList) {
    String status = statusService.getStatus(statusUpdate.getStatusType());
    String statusExplanation = statusExplanationService.getStatusExplanation(statusUpdate.getStatusType());
    String processingLabel = processingLabelService.getProcessingLabel(statusUpdate);
    String processingDescription = processingDescriptionService.getProcessingDescription(statusUpdate);
    List<StatusItemRfiView> statusItemRfiViews = rfiList.stream()
        .sorted(Comparator.comparing(Rfi::getReceivedTimestamp))
        .map(this::getStatusItemRviView)
        .collect(Collectors.toList());
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, statusItemRfiViews);
  }

  private StatusItemRfiView getStatusItemRviView(Rfi rfi) {
    String time = timeFormatService.formatDateAndTime(rfi.getReceivedTimestamp());
    String description = "Received on " + time;
    return new StatusItemRfiView(rfi.getAppId(), rfi.getRfiId(), description);
  }

}

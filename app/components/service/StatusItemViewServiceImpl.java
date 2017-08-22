package components.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.RfiDao;
import components.dao.StatusUpdateDao;
import models.Application;
import models.Rfi;
import models.StatusUpdate;
import models.enums.StatusType;
import models.view.StatusItemRfiView;
import models.view.StatusItemView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatusItemViewServiceImpl implements StatusItemViewService {

  private final static List<StatusType> STATUS_TYPE_LIST = Arrays.asList(
      StatusType.INITIAL_CHECKS,
      StatusType.TECHNICAL_ASSESSMENT,
      StatusType.LU_PROCESSING,
      StatusType.WITH_OGD,
      StatusType.FINAL_ASSESSMENT,
      StatusType.COMPLETE);

  private final StatusUpdateDao statusUpdateDao;
  private final RfiDao rfiDao;
  private final TimeFormatService timeFormatService;
  private final ProcessingDescriptionService processingDescriptionService;
  private final StatusExplanationService statusExplanationService;
  private final ProcessingLabelService processingLabelService;
  private final StatusService statusService;
  private final ApplicationDao applicationDao;

  @Inject
  public StatusItemViewServiceImpl(StatusUpdateDao statusUpdateDao,
                                   RfiDao rfiDao,
                                   TimeFormatService timeFormatService,
                                   ProcessingDescriptionService processingDescriptionService,
                                   StatusExplanationService statusExplanationService,
                                   ProcessingLabelService processingLabelService,
                                   StatusService statusService,
                                   ApplicationDao applicationDao) {
    this.statusUpdateDao = statusUpdateDao;
    this.rfiDao = rfiDao;
    this.timeFormatService = timeFormatService;
    this.processingDescriptionService = processingDescriptionService;
    this.statusExplanationService = statusExplanationService;
    this.processingLabelService = processingLabelService;
    this.statusService = statusService;
    this.applicationDao = applicationDao;
  }

  @Override
  public List<StatusItemView> getStatusItemViews(String appId) {

    Application application = applicationDao.getApplication(appId);
    StatusItemView draftStatusItemView = createDraftStatusItemView(application);
    StatusItemView submittedStatusItemView = createSubmittedStatusItemView(application);
    List<StatusItemView> updateStatusItemViews = createUpdateStatusItemViews(appId);

    List<StatusItemView> statusItemViews = new ArrayList<>();
    statusItemViews.add(draftStatusItemView);
    statusItemViews.add(submittedStatusItemView);
    statusItemViews.addAll(updateStatusItemViews);
    return statusItemViews;
  }

  private StatusItemView createDraftStatusItemView(Application application) {
    String status = statusService.getDraft();
    String statusExplanation = statusExplanationService.getDraftStatusExplanation();
    String processingLabel = processingLabelService.getDraftProcessingLabel();
    String processingDescription = processingDescriptionService.getDraftProcessingDescription(application);
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, new ArrayList<>());
  }

  private StatusItemView createSubmittedStatusItemView(Application application) {
    String status = statusService.getSubmitted();
    String statusExplanation = statusExplanationService.getSubmittedStatusExplanation();
    String processingLabel = processingLabelService.getSubmittedProcessingLabel(application);
    String processingDescription = processingDescriptionService.getSubmittedProcessingDescription(application);
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, new ArrayList<>());
  }

  private List<StatusItemView> createUpdateStatusItemViews(String appId) {
    List<StatusUpdate> statusUpdates = getStatusUpdates(appId);
    Multimap<StatusUpdate, Rfi> rfiMultimap = createRfiMultimap(appId, statusUpdates);
    return statusUpdates.stream()
        .map(statusUpdate -> getStatusItemView(statusUpdate, rfiMultimap.get(statusUpdate)))
        .collect(Collectors.toList());
  }

  private List<StatusUpdate> getStatusUpdates(String appId) {
    Map<StatusType, StatusUpdate> statusUpdateMap = new HashMap<>();
    statusUpdateDao.getStatusUpdates(appId).forEach(su -> statusUpdateMap.put(su.getStatusType(), su));
    return STATUS_TYPE_LIST.stream().map(statusType -> {
      StatusUpdate statusUpdate = statusUpdateMap.get(statusType);
      if (statusUpdate != null) {
        return statusUpdate;
      } else {
        return new StatusUpdate(appId, statusType, null, null);
      }
    }).collect(Collectors.toList());
  }

  private StatusItemView getStatusItemView(StatusUpdate statusUpdate, Collection<Rfi> rfiList) {
    String status = statusService.getStatus(statusUpdate.getStatusType());
    String statusExplanation = statusExplanationService.getStatusExplanation(statusUpdate.getStatusType());
    String processingLabel = processingLabelService.getProcessingLabel(statusUpdate);
    String processingDescription = processingDescriptionService.getProcessingDescription(statusUpdate);
    List<StatusItemRfiView> statusItemRfiViews = rfiList.stream()
        .sorted(Comparator.comparing(Rfi::getReceivedTimestamp))
        .map(this::getStatusItemRfiView)
        .collect(Collectors.toList());
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, statusItemRfiViews);
  }

  private StatusItemRfiView getStatusItemRfiView(Rfi rfi) {
    String time = timeFormatService.formatDateAndTime(rfi.getReceivedTimestamp());
    String description = "Received on " + time;
    return new StatusItemRfiView(rfi.getAppId(), rfi.getRfiId(), description);
  }

  private Multimap<StatusUpdate, Rfi> createRfiMultimap(String appId, List<StatusUpdate> statusUpdates) {
    List<Rfi> sortedRfiList = rfiDao.getRfiList(appId).stream()
        .sorted(Comparator.comparing(Rfi::getReceivedTimestamp))
        .collect(Collectors.toList());
    Multimap<StatusUpdate, Rfi> rfiMap = HashMultimap.create();
    for (Rfi rfi : sortedRfiList) {
      for (StatusUpdate statusUpdate : Lists.reverse(statusUpdates)) {
        if (statusUpdate.getStartTimestamp() != null && rfi.getReceivedTimestamp() >= statusUpdate.getStartTimestamp()) {
          rfiMap.put(statusUpdate, rfi);
          break;
        }
      }
    }
    return rfiMap;
  }

}

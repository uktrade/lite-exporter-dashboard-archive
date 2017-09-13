package components.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.RfiDao;
import components.dao.RfiResponseDao;
import components.dao.StatusUpdateDao;
import components.util.ApplicationUtil;
import components.util.TimeUtil;
import models.Application;
import models.Rfi;
import models.RfiResponse;
import models.StatusUpdate;
import models.enums.StatusType;
import models.view.StatusItemRfiView;
import models.view.StatusItemView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatusItemViewServiceImpl implements StatusItemViewService {

  private final StatusUpdateDao statusUpdateDao;
  private final RfiDao rfiDao;
  private final ApplicationDao applicationDao;
  private final RfiResponseDao rfiResponseDao;

  @Inject
  public StatusItemViewServiceImpl(StatusUpdateDao statusUpdateDao,
                                   RfiDao rfiDao,
                                   ApplicationDao applicationDao,
                                   RfiResponseDao rfiResponseDao) {
    this.statusUpdateDao = statusUpdateDao;
    this.rfiDao = rfiDao;
    this.applicationDao = applicationDao;
    this.rfiResponseDao = rfiResponseDao;
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
    String status = ApplicationUtil.DRAFT;
    String statusExplanation = "";
    String processingLabel = "Finished";
    String processingDescription = "Created on " + TimeUtil.formatDate(application.getCreatedTimestamp());
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, new ArrayList<>());
  }

  private StatusItemView createSubmittedStatusItemView(Application application) {
    String status = ApplicationUtil.SUBMITTED;
    String statusExplanation = "";
    String processingLabel;
    String processingDescription;
    if (application.getSubmittedTimestamp() == null) {
      processingLabel = "Not started";
      processingDescription = "";
    } else {
      processingLabel = "Finished";
      processingDescription = "Submitted on " + TimeUtil.formatDate(application.getSubmittedTimestamp());
    }
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, new ArrayList<>());
  }

  private List<StatusItemView> createUpdateStatusItemViews(String appId) {
    List<StatusUpdate> statusUpdates = getStatusUpdates(appId);
    List<Rfi> rfiList = rfiDao.getRfiList(appId);
    Multimap<StatusUpdate, Rfi> rfiMultimap = createRfiMultimap(appId, statusUpdates, rfiList);
    Map<String, RfiResponse> rfiIdToRfiResponseMap = createRfiIdToRfiResponseMap(rfiList);

    return statusUpdates.stream()
        .map(statusUpdate -> getStatusItemView(statusUpdate, rfiMultimap.get(statusUpdate), rfiIdToRfiResponseMap))
        .collect(Collectors.toList());
  }

  private Map<String, RfiResponse> createRfiIdToRfiResponseMap(List<Rfi> rfiList) {
    List<String> rfiIds = rfiList.stream()
        .map(Rfi::getRfiId)
        .collect(Collectors.toList());
    Map<String, RfiResponse> rfiIdToRfiResponseMap = new HashMap<>();
    rfiResponseDao.getRfiResponses(rfiIds).forEach(rfiResponse -> rfiIdToRfiResponseMap.put(rfiResponse.getRfiId(), rfiResponse));
    return rfiIdToRfiResponseMap;
  }

  private List<StatusUpdate> getStatusUpdates(String appId) {
    Map<StatusType, StatusUpdate> statusUpdateMap = new EnumMap<>(StatusType.class);
    statusUpdateDao.getStatusUpdates(appId).forEach(su -> statusUpdateMap.put(su.getStatusType(), su));
    return ApplicationUtil.getAscendingStatusTypeList().stream().map(statusType -> {
      StatusUpdate statusUpdate = statusUpdateMap.get(statusType);
      if (statusUpdate != null) {
        return statusUpdate;
      } else {
        return new StatusUpdate(appId, statusType, null, null);
      }
    }).collect(Collectors.toList());
  }

  private StatusItemView getStatusItemView(StatusUpdate statusUpdate, Collection<Rfi> rfiList, Map<String, RfiResponse> rfiIdToRfiResponseMap) {
    String status = ApplicationUtil.getStatusName(statusUpdate.getStatusType());
    String statusExplanation = ApplicationUtil.getStatusExplanation(statusUpdate.getStatusType());
    String processingLabel = getProcessingLabel(statusUpdate);
    String processingDescription = getProcessingDescription(statusUpdate);
    List<StatusItemRfiView> statusItemRfiViews = rfiList.stream()
        .sorted(Comparator.comparing(Rfi::getReceivedTimestamp))
        .map(rfi -> getStatusItemRfiView(rfi, rfiIdToRfiResponseMap.get(rfi.getRfiId())))
        .collect(Collectors.toList());
    return new StatusItemView(status, statusExplanation, processingLabel, processingDescription, statusItemRfiViews);
  }

  private String getProcessingLabel(StatusUpdate statusUpdate) {
    if (statusUpdate.getStatusType() == StatusType.COMPLETE) {
      if (statusUpdate.getStartTimestamp() != null || statusUpdate.getEndTimestamp() != null) {
        return "Finished";
      } else {
        return "Not started";
      }
    } else if (statusUpdate.getStartTimestamp() == null && statusUpdate.getEndTimestamp() == null) {
      return "Not started";
    } else if (statusUpdate.getEndTimestamp() != null) {
      return "Finished";
    } else {
      return "In progress";
    }
  }

  private String getProcessingDescription(StatusUpdate statusUpdate) {
    if (statusUpdate.getStatusType() == StatusType.COMPLETE) {
      if (statusUpdate.getStartTimestamp() == null) {
        return "";
      } else {
        return "Decision reached on " + TimeUtil.formatDate(statusUpdate.getStartTimestamp());
      }
    } else {
      Long startTimestamp = statusUpdate.getStartTimestamp();
      Long endTimestamp = statusUpdate.getEndTimestamp();
      if (startTimestamp != null) {
        if (endTimestamp != null) {
          long duration = TimeUtil.daysBetweenWithStartBeforeEnd(startTimestamp, endTimestamp);
          return "Processed in " + duration + " working days";
        } else {
          String started = TimeUtil.formatDate(startTimestamp);
          long duration = TimeUtil.daysBetweenWithStartBeforeEnd(startTimestamp, Instant.now().toEpochMilli());
          return String.format("Started on %s<br>(%d days ago)", started, duration);
        }
      } else {
        return "";
      }
    }
  }

  private StatusItemRfiView getStatusItemRfiView(Rfi rfi, RfiResponse rfiResponse) {
    if (rfiResponse == null) {
      String time = TimeUtil.formatDateAndTime(rfi.getReceivedTimestamp());
      String description = "Received on " + time;
      return new StatusItemRfiView(rfi.getAppId(), rfi.getRfiId(), description);
    } else {
      String time = TimeUtil.formatDateAndTime(rfiResponse.getSentTimestamp());
      String description = "Replied to on " + time;
      return new StatusItemRfiView(rfi.getAppId(), rfi.getRfiId(), description);
    }
  }

  private Multimap<StatusUpdate, Rfi> createRfiMultimap(String appId, List<StatusUpdate> statusUpdates, List<Rfi> rfiList) {
    List<Rfi> sortedRfiList = rfiList.stream()
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

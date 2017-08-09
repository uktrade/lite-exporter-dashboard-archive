package components.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.StatusUpdateDao;
import models.Application;
import models.StatusUpdate;
import models.enums.SortDirection;
import models.enums.StatusType;
import models.view.ApplicationItemView;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ApplicationItemViewServiceImpl implements ApplicationItemViewService {

  private final ApplicationDao applicationDao;
  private final StatusUpdateDao statusUpdateDao;
  private final TimeFormatService timeFormatService;
  private final StatusService statusService;

  @Inject
  public ApplicationItemViewServiceImpl(ApplicationDao applicationDao, StatusUpdateDao statusUpdateDao, TimeFormatService timeFormatService, StatusService statusService) {
    this.applicationDao = applicationDao;
    this.statusUpdateDao = statusUpdateDao;
    this.timeFormatService = timeFormatService;
    this.statusService = statusService;
  }

  @Override
  public List<ApplicationItemView> getApplicationItemViews(SortDirection dateSortDirection, SortDirection statusSortDirection) {
    List<Application> applications = applicationDao.getApplications();
    Multimap<String, StatusUpdate> statusUpdateMap = HashMultimap.create();
    statusUpdateDao.getStatusUpdates().forEach(statusUpdate -> statusUpdateMap.put(statusUpdate.getAppId(), statusUpdate));
    List<ApplicationItemView> applicationItemViews = applications.stream()
        .map(application -> {
          Collection<StatusUpdate> statusUpdates = statusUpdateMap.get(application.getAppId());
          return getApplicationItemView(application, statusUpdates);
        })
        .collect(Collectors.toList());

    if (dateSortDirection == SortDirection.ASC) {
      applicationItemViews.sort(Comparator.comparing(ApplicationItemView::getDateSubmittedTimestamp));
    } else if (dateSortDirection == SortDirection.DESC) {
      applicationItemViews.sort(Comparator.comparing(ApplicationItemView::getDateSubmittedTimestamp).reversed());
    }

    if (statusSortDirection == SortDirection.ASC) {
      applicationItemViews.sort(Comparator.comparing(ApplicationItemView::getApplicationStatusTimestamp));
    } else if (statusSortDirection == SortDirection.DESC) {
      applicationItemViews.sort(Comparator.comparing(ApplicationItemView::getApplicationStatusTimestamp).reversed());
    }

    return applicationItemViews;
  }

  private ApplicationItemView getApplicationItemView(Application application, Collection<StatusUpdate> statusUpdates) {
    Optional<StatusUpdate> statusUpdate = statusUpdates.stream().filter(su -> su.getStatusType() == StatusType.SUBMITTED).findAny();
    if (!statusUpdate.isPresent()) {
      statusUpdate = statusUpdates.stream().filter(su -> su.getStatusType() == StatusType.DRAFT).findAny();
    }
    long dateSubmittedTimestamp = 0;
    String dateSubmitted = "";
    if (statusUpdate.isPresent() && statusUpdate.get().getStartTimestamp() != null) {
      dateSubmittedTimestamp = statusUpdate.get().getStartTimestamp();
      dateSubmitted = timeFormatService.formatDateWithSlashes(statusUpdate.get().getStartTimestamp());
    }
    String caseDescription = "";
    if (application.getCaseReference() != null) {
      caseDescription = String.format("Purchase order: %s", application.getCaseReference());
    }
    StatusType statusType = null;
    String applicationStatus = "";
    String applicationStatusDate = "";
    long applicationStatusTimestamp = 0;
    if (!statusUpdates.isEmpty()) {
      StatusUpdate maxStatusUpdate = Collections.max(statusUpdates, Comparator.comparing(StatusUpdate::getStartTimestamp, Comparator.nullsFirst(Comparator.naturalOrder())));
      if (maxStatusUpdate != null) {
        applicationStatus = statusService.getStatus(maxStatusUpdate.getStatusType());
        if (maxStatusUpdate.getStartTimestamp() != null) {
          applicationStatusDate = String.format("Since: %s", timeFormatService.formatDateWithSlashes(maxStatusUpdate.getStartTimestamp()));
          applicationStatusTimestamp = maxStatusUpdate.getStartTimestamp();
        }
        if (maxStatusUpdate.getStatusType() != null) {
          statusType = maxStatusUpdate.getStatusType();
        }
      }
    }
    String destination = "";
    int destinationCount = application.getDestinationList().size();
    if (destinationCount == 1) {
      destination = application.getDestinationList().get(0);
    } else if (destinationCount > 1) {
      destination = String.format("%d destinations", destinationCount);
    }
    return new ApplicationItemView(application.getAppId(), dateSubmittedTimestamp, dateSubmitted, caseDescription, statusType, applicationStatus, applicationStatusDate, applicationStatusTimestamp, destination);
  }

}

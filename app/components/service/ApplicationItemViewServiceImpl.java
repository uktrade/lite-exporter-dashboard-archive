package components.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.RfiDao;
import components.dao.RfiResponseDao;
import components.dao.StatusUpdateDao;
import models.Application;
import models.Rfi;
import models.RfiResponse;
import models.StatusUpdate;
import models.enums.SortDirection;
import models.enums.StatusType;
import models.view.ApplicationItemView;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationItemViewServiceImpl implements ApplicationItemViewService {

  private static final Map<SortDirection, Comparator<ApplicationItemView>> DATE_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<ApplicationItemView>> STATUS_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<ApplicationItemView>> CREATED_BY_COMPARATORS = new EnumMap<>(SortDirection.class);

  static {
    Comparator<ApplicationItemView> dateComparator = Comparator.comparing(ApplicationItemView::getDateSubmittedTimestamp);
    DATE_COMPARATORS.put(SortDirection.ASC, dateComparator);
    DATE_COMPARATORS.put(SortDirection.DESC, dateComparator.reversed());
    Comparator<ApplicationItemView> statusComparator = Comparator.comparing(ApplicationItemView::getApplicationStatusTimestamp);
    STATUS_COMPARATORS.put(SortDirection.ASC, statusComparator);
    STATUS_COMPARATORS.put(SortDirection.DESC, statusComparator.reversed());
    Comparator<ApplicationItemView> createdByComparator = Comparator.comparing(ApplicationItemView::getApplicantReference);
    CREATED_BY_COMPARATORS.put(SortDirection.ASC, createdByComparator);
    CREATED_BY_COMPARATORS.put(SortDirection.DESC, createdByComparator.reversed());
  }


  private final ApplicationDao applicationDao;
  private final StatusUpdateDao statusUpdateDao;
  private final TimeFormatService timeFormatService;
  private final StatusService statusService;
  private final RfiDao rfiDao;
  private final RfiResponseDao rfiResponseDao;
  private final ApplicationSummaryViewService applicationSummaryViewService;

  @Inject
  public ApplicationItemViewServiceImpl(ApplicationDao applicationDao,
                                        StatusUpdateDao statusUpdateDao,
                                        TimeFormatService timeFormatService,
                                        StatusService statusService,
                                        RfiDao rfiDao,
                                        RfiResponseDao rfiResponseDao,
                                        ApplicationSummaryViewService applicationSummaryViewService) {
    this.applicationDao = applicationDao;
    this.statusUpdateDao = statusUpdateDao;
    this.timeFormatService = timeFormatService;
    this.statusService = statusService;
    this.rfiDao = rfiDao;
    this.rfiResponseDao = rfiResponseDao;
    this.applicationSummaryViewService = applicationSummaryViewService;
  }

  @Override
  public List<ApplicationItemView> getApplicationItemViews(SortDirection dateSortDirection, SortDirection statusSortDirection, SortDirection createdBySortDirection) {
    Multimap<String, StatusUpdate> appIdToStatusUpdateMap = HashMultimap.create();
    statusUpdateDao.getStatusUpdates().forEach(statusUpdate -> appIdToStatusUpdateMap.put(statusUpdate.getAppId(), statusUpdate));
    Map<String, String> appIdToOpenRfiIdMap = getAppIdToOpenRfiIdMap();

    List<Application> applications = applicationDao.getApplications();
    List<ApplicationItemView> applicationItemViews = applications.stream()
        .map(application -> {
          String appId = application.getAppId();
          Collection<StatusUpdate> statusUpdates = appIdToStatusUpdateMap.get(appId);
          String openRfiId = appIdToOpenRfiIdMap.get(appId);
          return getApplicationItemView(application, statusUpdates, openRfiId);
        })
        .collect(Collectors.toList());

    sort(applicationItemViews, dateSortDirection, statusSortDirection, createdBySortDirection);

    return applicationItemViews;
  }

  private void sort(List<ApplicationItemView> applicationItemViews, SortDirection dateSortDirection, SortDirection statusSortDirection, SortDirection createdBySortDirection) {
    if (dateSortDirection != null) {
      applicationItemViews.sort(DATE_COMPARATORS.get(dateSortDirection));
    }
    if (statusSortDirection != null) {
      applicationItemViews.sort(STATUS_COMPARATORS.get(statusSortDirection));
    }
    if (createdBySortDirection != null) {
      applicationItemViews.sort(CREATED_BY_COMPARATORS.get(createdBySortDirection));
    }
  }

  private ApplicationItemView getApplicationItemView(Application application, Collection<StatusUpdate> statusUpdates, String openRfiId) {

    long dateSubmittedTimestamp = getDateSubmittedTimestamp(statusUpdates);
    String dateSubmitted = getDateSubmitted(dateSubmittedTimestamp);

    StatusType statusType = null;
    String applicationStatus = "";
    String applicationStatusDate = "";
    long applicationStatusTimestamp = 0;

    StatusUpdate maxStatusUpdate = applicationSummaryViewService.getMaxStatusUpdate(statusUpdates).orElse(null);
    if (maxStatusUpdate != null) {
      applicationStatus = statusService.getStatus(maxStatusUpdate.getStatusType());
      Long maxTimestamp = maxStatusUpdate.getStartTimestamp();
      if (maxTimestamp != null) {
        applicationStatusDate = String.format("Since: %s", timeFormatService.formatDateWithSlashes(maxTimestamp));
        applicationStatusTimestamp = maxTimestamp;
      }
      statusType = maxStatusUpdate.getStatusType();
    }

    String destination = applicationSummaryViewService.getDestination(application);
    return new ApplicationItemView(application.getAppId(),
        application.getCompanyId(),
        application.getCompanyName(),
        application.getApplicantReference(),
        dateSubmittedTimestamp,
        dateSubmitted,
        application.getCaseReference(),
        statusType,
        applicationStatus,
        applicationStatusDate,
        applicationStatusTimestamp,
        destination,
        openRfiId
    );
  }

  private Map<String, String> getAppIdToOpenRfiIdMap() {
    Map<String, String> appIdToOpenRfiIdMap = new HashMap<>();
    Set<String> answeredRfiIds = rfiResponseDao.getRfiResponses().stream().map(RfiResponse::getRfiId).collect(Collectors.toSet());
    Multimap<String, Rfi> appIdToRfi = HashMultimap.create();
    rfiDao.getRfiList().forEach(rfi -> appIdToRfi.put(rfi.getAppId(), rfi));
    appIdToRfi.asMap().forEach((key, value) -> appIdToOpenRfiIdMap.put(key, getOpenRfiId(value, answeredRfiIds)));
    return appIdToOpenRfiIdMap;
  }

  private String getOpenRfiId(Collection<Rfi> rfiList, Set<String> answeredRfiIds) {
    return rfiList.stream()
        .filter(rfi -> !answeredRfiIds.contains(rfi.getRfiId()))
        .sorted(Comparator.comparing(Rfi::getReceivedTimestamp).reversed())
        .map(Rfi::getRfiId)
        .findFirst().orElse(null);
  }

  private long getDateSubmittedTimestamp(Collection<StatusUpdate> statusUpdates) {
    Optional<StatusUpdate> statusUpdate = statusUpdates.stream().filter(su -> su.getStatusType() == StatusType.SUBMITTED).findAny();
    if (!statusUpdate.isPresent()) {
      statusUpdate = statusUpdates.stream().filter(su -> su.getStatusType() == StatusType.DRAFT).findAny();
    }
    if (statusUpdate.isPresent() && statusUpdate.get().getStartTimestamp() != null) {
      return statusUpdate.get().getStartTimestamp();
    } else {
      return 0;
    }
  }

  private String getDateSubmitted(long dateSubmittedTimestamp) {
    if (dateSubmittedTimestamp == 0) {
      return "";
    } else {
      return timeFormatService.formatDateWithSlashes(dateSubmittedTimestamp);
    }
  }

}

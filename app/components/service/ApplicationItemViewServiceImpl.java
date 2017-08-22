package components.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.comparator.ApplicationDateComparator;
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
import uk.gov.bis.lite.customer.api.view.CustomerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationItemViewServiceImpl implements ApplicationItemViewService {

  private static final Map<SortDirection, Comparator<ApplicationItemView>> DATE_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<ApplicationItemView>> STATUS_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<ApplicationItemView>> CREATED_BY_COMPARATORS = new EnumMap<>(SortDirection.class);

  static {
    DATE_COMPARATORS.put(SortDirection.ASC, new ApplicationDateComparator());
    DATE_COMPARATORS.put(SortDirection.DESC, new ApplicationDateComparator().reversed());
    Comparator<ApplicationItemView> statusComparator = Comparator.comparing(ApplicationItemView::getApplicationStatusTimestamp);
    STATUS_COMPARATORS.put(SortDirection.ASC, statusComparator);
    STATUS_COMPARATORS.put(SortDirection.DESC, statusComparator.reversed());
    Comparator<ApplicationItemView> createdByComparator = Comparator.comparing(ApplicationItemView::getCreatedBy);
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
  private final CustomerServiceClient customerServiceClient;
  private final UserService userService;

  @Inject
  public ApplicationItemViewServiceImpl(ApplicationDao applicationDao,
                                        StatusUpdateDao statusUpdateDao,
                                        TimeFormatService timeFormatService,
                                        StatusService statusService,
                                        RfiDao rfiDao,
                                        RfiResponseDao rfiResponseDao,
                                        ApplicationSummaryViewService applicationSummaryViewService,
                                        CustomerServiceClient customerServiceClient,
                                        UserService userService) {
    this.applicationDao = applicationDao;
    this.statusUpdateDao = statusUpdateDao;
    this.timeFormatService = timeFormatService;
    this.statusService = statusService;
    this.rfiDao = rfiDao;
    this.rfiResponseDao = rfiResponseDao;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.customerServiceClient = customerServiceClient;
    this.userService = userService;
  }

  @Override
  public List<ApplicationItemView> getApplicationItemViews(String userId, SortDirection dateSortDirection, SortDirection statusSortDirection, SortDirection createdBySortDirection) {
    List<CustomerView> customerViews = customerServiceClient.getCustomers(userId);

    Map<String, String> customerIdToCompanyName = customerViews.stream()
        .collect(Collectors.toMap(CustomerView::getCustomerId, CustomerView::getCompanyName));

    List<String> customerIds = new ArrayList<>(customerIdToCompanyName.keySet());

    List<Application> applications = applicationDao.getApplications(customerIds);
    List<String> appIds = applications.stream().map(Application::getAppId).collect(Collectors.toList());

    Multimap<String, StatusUpdate> appIdToStatusUpdateMap = HashMultimap.create();
    statusUpdateDao.getStatusUpdates(appIds).forEach(statusUpdate -> appIdToStatusUpdateMap.put(statusUpdate.getAppId(), statusUpdate));

    Map<String, String> appIdToOpenRfiIdMap = getAppIdToOpenRfiIdMap(appIds);

    List<ApplicationItemView> applicationItemViews = applications.stream()
        .map(application -> {
          String companyName = customerIdToCompanyName.get(application.getCompanyId());
          String appId = application.getAppId();
          Collection<StatusUpdate> statusUpdates = appIdToStatusUpdateMap.get(appId);
          String openRfiId = appIdToOpenRfiIdMap.get(appId);
          return getApplicationItemView(application, companyName, statusUpdates, openRfiId);
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

  private ApplicationItemView getApplicationItemView(Application application, String companyName, Collection<StatusUpdate> statusUpdates, String openRfiId) {

    StatusType statusType;
    String applicationStatus;
    long statusTimestamp;

    StatusUpdate maxStatusUpdate = applicationSummaryViewService.getMaxStatusUpdate(statusUpdates).orElse(null);
    if (maxStatusUpdate != null) {
      applicationStatus = statusService.getStatus(maxStatusUpdate.getStatusType());
      statusTimestamp = maxStatusUpdate.getStartTimestamp();
      statusType = maxStatusUpdate.getStatusType();
    } else {
      statusType = null;
      if (application.getSubmittedTimestamp() != null) {
        applicationStatus = statusService.getSubmitted();
        statusTimestamp = application.getSubmittedTimestamp();
      } else {
        applicationStatus = statusService.getDraft();
        statusTimestamp = application.getCreatedTimestamp();
      }
    }

    String date = getDate(application);
    String applicationStatusDate = String.format("Since: %s", timeFormatService.formatDateWithSlashes(statusTimestamp));

    String createdBy = userService.getUser(application.getCreatedBy()).getName();
    String destination = applicationSummaryViewService.getDestination(application);

    return new ApplicationItemView(application.getAppId(),
        application.getCompanyId(),
        companyName,
        createdBy,
        application.getCreatedTimestamp(),
        application.getSubmittedTimestamp(),
        date,
        application.getCaseReference(),
        application.getApplicantReference(),
        statusType,
        applicationStatus,
        applicationStatusDate,
        statusTimestamp,
        destination,
        openRfiId
    );
  }

  private String getDate(Application application) {
    long dateTimestamp;
    if (application.getSubmittedTimestamp() != null) {
      dateTimestamp = application.getSubmittedTimestamp();
    } else {
      dateTimestamp = application.getCreatedTimestamp();
    }
    return timeFormatService.formatDateWithSlashes(dateTimestamp);
  }

  private Map<String, String> getAppIdToOpenRfiIdMap(List<String> appIds) {
    List<Rfi> rfiList = rfiDao.getRfiList(appIds);
    Multimap<String, Rfi> appIdToRfi = HashMultimap.create();
    rfiList.forEach(rfi -> appIdToRfi.put(rfi.getAppId(), rfi));

    List<String> rfiIds = rfiList.stream()
        .map(Rfi::getRfiId)
        .collect(Collectors.toList());
    Set<String> answeredRfiIds = rfiResponseDao.getRfiResponses(rfiIds).stream()
        .map(RfiResponse::getRfiId)
        .collect(Collectors.toSet());

    Map<String, String> appIdToOpenRfiIdMap = new HashMap<>();
    appIdToRfi.asMap().forEach((appId, rfiCollection) -> {
      String openRfiId = getOpenRfiId(rfiCollection, answeredRfiIds);
      appIdToOpenRfiIdMap.put(appId, openRfiId);
    });
    return appIdToOpenRfiIdMap;
  }

  private String getOpenRfiId(Collection<Rfi> rfiList, Set<String> answeredRfiIds) {
    return rfiList.stream()
        .filter(rfi -> !answeredRfiIds.contains(rfi.getRfiId()))
        .sorted(Comparator.comparing(Rfi::getReceivedTimestamp).reversed())
        .map(Rfi::getRfiId)
        .findFirst()
        .orElse(null);
  }

}

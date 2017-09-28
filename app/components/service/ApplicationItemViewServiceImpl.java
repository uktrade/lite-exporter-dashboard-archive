package components.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.dao.ApplicationDao;
import components.dao.RfiDao;
import components.dao.RfiReplyDao;
import components.dao.StatusUpdateDao;
import components.util.ApplicationUtil;
import components.util.TimeUtil;
import models.Application;
import models.Rfi;
import models.StatusUpdate;
import models.User;
import models.enums.ApplicationProgress;
import models.enums.StatusType;
import models.view.ApplicationItemView;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.exporterdashboard.api.RfiReply;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationItemViewServiceImpl implements ApplicationItemViewService {

  private final ApplicationDao applicationDao;
  private final StatusUpdateDao statusUpdateDao;
  private final RfiDao rfiDao;
  private final RfiReplyDao rfiReplyDao;
  private final CustomerServiceClient customerServiceClient;
  private final UserService userService;

  @Inject
  public ApplicationItemViewServiceImpl(ApplicationDao applicationDao,
                                        StatusUpdateDao statusUpdateDao,
                                        RfiDao rfiDao,
                                        RfiReplyDao rfiReplyDao,
                                        CustomerServiceClient customerServiceClient,
                                        UserService userService) {
    this.applicationDao = applicationDao;
    this.statusUpdateDao = statusUpdateDao;
    this.rfiDao = rfiDao;
    this.rfiReplyDao = rfiReplyDao;
    this.customerServiceClient = customerServiceClient;
    this.userService = userService;
  }

  @Override
  public List<ApplicationItemView> getApplicationItemViews(String userId) {
    List<CustomerView> customerViews = customerServiceClient.getCustomers(userId);

    Map<String, String> customerIdToCompanyName = customerViews.stream()
        .collect(Collectors.toMap(CustomerView::getCustomerId, CustomerView::getCompanyName));

    List<String> customerIds = new ArrayList<>(customerIdToCompanyName.keySet());

    List<Application> applications = applicationDao.getApplications(customerIds);
    List<String> appIds = applications.stream().map(Application::getAppId).collect(Collectors.toList());

    Multimap<String, StatusUpdate> appIdToStatusUpdateMap = HashMultimap.create();
    statusUpdateDao.getStatusUpdates(appIds).forEach(statusUpdate -> appIdToStatusUpdateMap.put(statusUpdate.getAppId(), statusUpdate));

    Map<String, String> appIdToOpenRfiIdMap = getAppIdToOpenRfiIdMap(appIds);

    return applications.stream()
        .map(application -> {
          String companyName = customerIdToCompanyName.get(application.getCompanyId());
          String appId = application.getAppId();
          Collection<StatusUpdate> statusUpdates = appIdToStatusUpdateMap.get(appId);
          String openRfiId = appIdToOpenRfiIdMap.get(appId);
          return getApplicationItemView(application, companyName, statusUpdates, openRfiId);
        })
        .collect(Collectors.toList());
  }

  private ApplicationItemView getApplicationItemView(Application application, String companyName, Collection<StatusUpdate> statusUpdates, String openRfiId) {

    String applicationStatus;
    long statusTimestamp;

    StatusUpdate maxStatusUpdate = ApplicationUtil.getMaxStatusUpdate(statusUpdates);
    if (maxStatusUpdate != null) {
      applicationStatus = ApplicationUtil.getStatusName(maxStatusUpdate.getStatusType());
      statusTimestamp = maxStatusUpdate.getCreatedTimestamp();
    } else if (application.getSubmittedTimestamp() != null) {
      applicationStatus = ApplicationUtil.SUBMITTED;
      statusTimestamp = application.getSubmittedTimestamp();
    } else {
      applicationStatus = ApplicationUtil.DRAFT;
      statusTimestamp = application.getCreatedTimestamp();
    }

    Long dateTimestamp = getDateTimestamp(maxStatusUpdate, application);
    String date = TimeUtil.formatDate(dateTimestamp);

    String applicationStatusDate;
    if (maxStatusUpdate != null && StatusType.COMPLETE == maxStatusUpdate.getStatusType()) {
      applicationStatusDate = "On " + TimeUtil.formatDate(statusTimestamp);
    } else {
      applicationStatusDate = "Since " + TimeUtil.formatDate(statusTimestamp);
    }

    String createdById = application.getCreatedBy();
    User user = userService.getUser(createdById);
    String destination = ApplicationUtil.getDestinations(application.getDestinationList());

    ApplicationProgress applicationProgress = getApplicationProgress(maxStatusUpdate, application);

    return new

        ApplicationItemView(application.getAppId(),
        application.getCompanyId(),
        companyName,
        createdById,
        user.getFirstName(),
        user.getLastName(),
        dateTimestamp,
        date,
        application.getCaseReference(),
        application.getApplicantReference(),
        applicationProgress,
        applicationStatus,
        applicationStatusDate,
        statusTimestamp,
        destination,
        openRfiId
    );
  }

  private ApplicationProgress getApplicationProgress(StatusUpdate maxStatusUpdate, Application application) {
    if (maxStatusUpdate != null && maxStatusUpdate.getStatusType() == StatusType.COMPLETE) {
      return ApplicationProgress.COMPLETED;
    } else if (application.getSubmittedTimestamp() != null) {
      return ApplicationProgress.CURRENT;
    } else {
      return ApplicationProgress.DRAFT;
    }
  }

  private Long getDateTimestamp(StatusUpdate maxStatusUpdate, Application application) {
    if (maxStatusUpdate != null && maxStatusUpdate.getStatusType() == StatusType.COMPLETE) {
      return maxStatusUpdate.getCreatedTimestamp();
    } else if (application.getSubmittedTimestamp() != null) {
      return application.getSubmittedTimestamp();
    } else {
      return application.getCreatedTimestamp();
    }
  }

  private Map<String, String> getAppIdToOpenRfiIdMap(List<String> appIds) {
    List<Rfi> rfiList = rfiDao.getRfiList(appIds);
    Multimap<String, Rfi> appIdToRfi = HashMultimap.create();
    rfiList.forEach(rfi -> appIdToRfi.put(rfi.getAppId(), rfi));

    List<String> rfiIds = rfiList.stream()
        .map(Rfi::getRfiId)
        .collect(Collectors.toList());
    Set<String> answeredRfiIds = rfiReplyDao.getRfiReplies(rfiIds).stream()
        .map(RfiReply::getId)
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

package components.service;

import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.StatusUpdateDao;
import models.Application;
import models.StatusUpdate;
import models.enums.StatusType;
import models.view.ApplicationSummaryView;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ApplicationSummaryViewServiceImpl implements ApplicationSummaryViewService {

  private static final List<StatusType> INVERSE_STATUS_TYPE_LIST;

  static {
    List<StatusType> statusTypeList = Arrays.asList(
        StatusType.INITIAL_CHECKS,
        StatusType.TECHNICAL_ASSESSMENT,
        StatusType.LU_PROCESSING,
        StatusType.WITH_OGD,
        StatusType.FINAL_ASSESSMENT,
        StatusType.COMPLETE);
    Collections.reverse(statusTypeList);
    INVERSE_STATUS_TYPE_LIST = Collections.unmodifiableList(statusTypeList);
  }

  private final StatusUpdateDao statusUpdateDao;
  private final ApplicationDao applicationDao;
  private final TimeFormatService timeFormatService;
  private final UserService userService;
  private final StatusService statusService;

  @Inject
  public ApplicationSummaryViewServiceImpl(StatusUpdateDao statusUpdateDao, ApplicationDao applicationDao, TimeFormatService timeFormatService, UserService userService, StatusService statusService) {
    this.statusUpdateDao = statusUpdateDao;
    this.applicationDao = applicationDao;
    this.timeFormatService = timeFormatService;
    this.userService = userService;
    this.statusService = statusService;
  }

  @Override
  public ApplicationSummaryView getApplicationSummaryView(String appId) {
    Application application = applicationDao.getApplication(appId);
    return new ApplicationSummaryView(appId,
        application.getCaseReference(),
        getDestination(application),
        getDateSubmitted(application),
        getStatus(appId),
        getOfficerName(application));
  }

  private String getStatus(String appId) {
    List<StatusUpdate> statusUpdates = statusUpdateDao.getStatusUpdates(appId);
    Optional<StatusUpdate> maxStatusUpdate = getMaxStatusUpdate(statusUpdates);
    if (maxStatusUpdate.isPresent()) {
      return statusService.getStatus(maxStatusUpdate.get().getStatusType());
    } else {
      return statusService.getSubmitted();
    }
  }

  @Override
  public String getDestination(Application application) {
    int destinationCount = application.getDestinationList().size();
    if (destinationCount == 1) {
      return application.getDestinationList().get(0);
    } else if (destinationCount > 1) {
      return String.format("%d destinations", destinationCount);
    } else {
      return "";
    }
  }

  private String getDateSubmitted(Application application) {
    return timeFormatService.formatDate(application.getSubmittedTimestamp());
  }

  @Override
  public Optional<StatusUpdate> getMaxStatusUpdate(Collection<StatusUpdate> statusUpdates) {
    if (CollectionUtils.isNotEmpty(statusUpdates)) {
      Map<StatusType, StatusUpdate> statusUpdateMap = new EnumMap<>(StatusType.class);
      statusUpdates.forEach(statusUpdate -> statusUpdateMap.put(statusUpdate.getStatusType(), statusUpdate));
      for (StatusType statusType : INVERSE_STATUS_TYPE_LIST) {
        StatusUpdate statusUpdate = statusUpdateMap.get(statusType);
        if (statusUpdate != null) {
          return Optional.of(statusUpdate);
        }
      }
    }
    return Optional.empty();
  }

  private String getOfficerName(Application application) {
    if (application.getCaseOfficerId() != null) {
      return userService.getUser(application.getCaseOfficerId()).getName();
    } else {
      return "Not assigned yet";
    }
  }

}

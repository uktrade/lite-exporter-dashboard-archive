package components.service;

import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.StatusUpdateDao;
import models.Application;
import models.StatusUpdate;
import models.view.ApplicationSummaryView;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ApplicationSummaryViewServiceImpl implements ApplicationSummaryViewService {

  private static final Comparator<StatusUpdate> MAX_STATUS_UPDATE_COMPARATOR = Comparator.comparing(StatusUpdate::getStartTimestamp, Comparator.nullsFirst(Comparator.naturalOrder()));

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
    List<StatusUpdate> statusUpdates = statusUpdateDao.getStatusUpdates(appId);
    Optional<StatusUpdate> maxStatusUpdate = getMaxStatusUpdate(statusUpdates);
    String status = "";
    if (maxStatusUpdate.isPresent()) {
      status = statusService.getStatus(maxStatusUpdate.get().getStatusType());
    }
    return new ApplicationSummaryView(appId,
        application.getCaseReference(),
        getDestination(application),
        getDateSubmitted(application),
        status,
        getOfficerName(application));
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
      StatusUpdate maxStatusUpdate = Collections.max(statusUpdates, MAX_STATUS_UPDATE_COMPARATOR);
      return Optional.ofNullable(maxStatusUpdate);
    } else {
      return Optional.empty();
    }
  }

  private String getOfficerName(Application application) {
    if (application.getCaseOfficerId() != null) {
      return userService.getUser(application.getCaseOfficerId()).getName();
    } else {
      return "Not assigned yet";
    }
  }

}

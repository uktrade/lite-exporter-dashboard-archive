package components.service;

import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.StatusUpdateDao;
import models.Application;
import models.StatusUpdate;
import models.view.ApplicationSummaryView;

import java.util.List;
import java.util.Optional;

public class ApplicationSummaryViewServiceImpl implements ApplicationSummaryViewService {

  private final StatusUpdateDao statusUpdateDao;
  private final ApplicationDao applicationDao;
  private final TimeFormatService timeFormatService;
  private final UserService userService;
  private final StatusService statusService;
  private final ApplicationService applicationService;

  @Inject
  public ApplicationSummaryViewServiceImpl(StatusUpdateDao statusUpdateDao,
                                           ApplicationDao applicationDao,
                                           TimeFormatService timeFormatService,
                                           UserService userService,
                                           StatusService statusService,
                                           ApplicationService applicationService) {
    this.statusUpdateDao = statusUpdateDao;
    this.applicationDao = applicationDao;
    this.timeFormatService = timeFormatService;
    this.userService = userService;
    this.statusService = statusService;
    this.applicationService = applicationService;
  }

  @Override
  public ApplicationSummaryView getApplicationSummaryView(String appId) {
    Application application = applicationDao.getApplication(appId);
    return new ApplicationSummaryView(application.getAppId(),
        application.getCaseReference(),
        application.getApplicantReference(),
        applicationService.getDestination(application),
        getDateSubmitted(application),
        getStatus(appId),
        getOfficerName(application));
  }

  private String getStatus(String appId) {
    List<StatusUpdate> statusUpdates = statusUpdateDao.getStatusUpdates(appId);
    Optional<StatusUpdate> maxStatusUpdate = applicationService.getMaxStatusUpdate(statusUpdates);
    if (maxStatusUpdate.isPresent()) {
      return statusService.getStatus(maxStatusUpdate.get().getStatusType());
    } else {
      return statusService.getSubmitted();
    }
  }

  private String getDateSubmitted(Application application) {
    return timeFormatService.formatDate(application.getSubmittedTimestamp());
  }

  private String getOfficerName(Application application) {
    if (application.getCaseOfficerId() != null) {
      return userService.getUser(application.getCaseOfficerId()).getName();
    } else {
      return "Not assigned yet";
    }
  }

}

package components.service;

import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.StatusUpdateDao;
import components.util.ApplicationUtil;
import components.util.TimeUtil;
import models.Application;
import models.StatusUpdate;
import models.view.ApplicationSummaryView;

import java.util.List;

public class ApplicationSummaryViewServiceImpl implements ApplicationSummaryViewService {

  private final StatusUpdateDao statusUpdateDao;
  private final ApplicationDao applicationDao;
  private final UserService userService;

  @Inject
  public ApplicationSummaryViewServiceImpl(StatusUpdateDao statusUpdateDao,
                                           ApplicationDao applicationDao,
                                           UserService userService) {
    this.statusUpdateDao = statusUpdateDao;
    this.applicationDao = applicationDao;
    this.userService = userService;
  }

  @Override
  public ApplicationSummaryView getApplicationSummaryView(String appId) {
    Application application = applicationDao.getApplication(appId);
    return new ApplicationSummaryView(application.getAppId(),
        application.getCaseReference(),
        application.getApplicantReference(),
        ApplicationUtil.getDestinations(application.getDestinationList()),
        getDateSubmitted(application),
        getStatus(appId),
        getOfficerName(application));
  }

  private String getStatus(String appId) {
    List<StatusUpdate> statusUpdates = statusUpdateDao.getStatusUpdates(appId);
    StatusUpdate maxStatusUpdate = ApplicationUtil.getMaxStatusUpdate(statusUpdates);
    if (maxStatusUpdate != null) {
      return ApplicationUtil.getStatusName(maxStatusUpdate.getStatusType());
    } else {
      return ApplicationUtil.SUBMITTED;
    }
  }

  private String getDateSubmitted(Application application) {
    return TimeUtil.formatDate(application.getSubmittedTimestamp());
  }

  private String getOfficerName(Application application) {
    if (application.getCaseOfficerId() != null) {
      return userService.getUsername(application.getCaseOfficerId());
    } else {
      return "Not assigned yet";
    }
  }

}

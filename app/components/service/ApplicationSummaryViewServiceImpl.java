package components.service;

import com.google.inject.Inject;
import components.util.ApplicationUtil;
import components.util.TimeUtil;
import models.AppData;
import models.Application;
import models.view.ApplicationSummaryView;

public class ApplicationSummaryViewServiceImpl implements ApplicationSummaryViewService {

  private final UserService userService;

  @Inject
  public ApplicationSummaryViewServiceImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public ApplicationSummaryView getApplicationSummaryView(AppData appData) {
    Application application = appData.getApplication();
    String dateSubmitted = TimeUtil.formatDate(application.getSubmittedTimestamp());
    String applicationStatus = ApplicationUtil.getStatusInfo(appData).getApplicationStatus();
    return new ApplicationSummaryView(application.getId(),
        appData.getCaseReference(),
        application.getApplicantReference(),
        ApplicationUtil.getDestinations(application),
        dateSubmitted,
        applicationStatus,
        getOfficerName(application));
  }

  private String getOfficerName(Application application) {
    if (application.getCaseOfficerId() != null) {
      return userService.getUsername(application.getCaseOfficerId());
    } else {
      return "Not yet assigned";
    }
  }

}

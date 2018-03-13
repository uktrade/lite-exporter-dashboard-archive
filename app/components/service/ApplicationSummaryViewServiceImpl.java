package components.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.util.ApplicationUtil;
import components.util.TimeUtil;
import models.AppData;
import models.Application;
import models.view.ApplicationSummaryView;

public class ApplicationSummaryViewServiceImpl implements ApplicationSummaryViewService {

  private final String licenceApplicationAddress;
  private final UserService userService;
  private final DestinationService destinationService;

  @Inject
  public ApplicationSummaryViewServiceImpl(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                                           UserService userService,
                                           DestinationService destinationService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.userService = userService;
    this.destinationService = destinationService;
  }

  @Override
  public ApplicationSummaryView getApplicationSummaryView(AppData appData) {
    Application application = appData.getApplication();
    String destination = destinationService.getDestination(application);
    String dateSubmitted = TimeUtil.formatDate(appData.getSubmittedTimestamp());
    String applicationStatus = ApplicationUtil.getStatusInfo(appData).getApplicationStatus();
    String licenceApplicationLink = licenceApplicationAddress + "/exporter-resume/" + application.getId();
    return new ApplicationSummaryView(application.getId(),
        appData.getCaseReference(),
        application.getApplicantReference(),
        destination,
        dateSubmitted,
        applicationStatus,
        getOfficerName(application),
        licenceApplicationLink);
  }

  private String getOfficerName(Application application) {
    if (application.getCaseOfficerId() != null) {
      return userService.getUsername(application.getCaseOfficerId());
    } else {
      return "Not yet assigned";
    }
  }

}

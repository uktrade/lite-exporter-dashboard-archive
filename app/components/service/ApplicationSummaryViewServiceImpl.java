package components.service;

import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.StatusUpdateDao;
import models.Application;
import models.StatusUpdate;
import models.enums.StatusType;
import models.view.ApplicationSummaryView;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ApplicationSummaryViewServiceImpl implements ApplicationSummaryViewService {

  private final StatusUpdateDao statusUpdateDao;
  private final ApplicationDao applicationDao;
  private final TimeFormatService timeFormatService;
  private final PersonService personService;
  private final StatusService statusService;

  @Inject
  public ApplicationSummaryViewServiceImpl(StatusUpdateDao statusUpdateDao, ApplicationDao applicationDao, TimeFormatService timeFormatService, PersonService personService, StatusService statusService) {
    this.statusUpdateDao = statusUpdateDao;
    this.applicationDao = applicationDao;
    this.timeFormatService = timeFormatService;
    this.personService = personService;
    this.statusService = statusService;
  }

  @Override
  public ApplicationSummaryView getApplicationSummaryView(String appId) {
    Application application = applicationDao.getApplication(appId);
    List<StatusUpdate> statusUpdates = statusUpdateDao.getStatusUpdates(appId);
    String officer = personService.getPerson(application.getCaseOfficerId());

    Optional<StatusUpdate> maxStatusUpdate = getMaxStatusUpdate(statusUpdates);
    String status = "";
    if (maxStatusUpdate.isPresent()) {
      status = statusService.getStatus(maxStatusUpdate.get().getStatusType());
    }
    return new ApplicationSummaryView(appId,
        application.getCaseReference(),
        getDestination(application),
        getDateSubmitted(statusUpdates),
        status,
        officer);
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

  private String getDateSubmitted(List<StatusUpdate> statusUpdateList) {
    Optional<StatusUpdate> submitted = statusUpdateList.stream().filter(statusUpdate -> StatusType.SUBMITTED == statusUpdate.getStatusType()).findAny();
    if (submitted.isPresent() && submitted.get().getStartTimestamp() != null) {
      return timeFormatService.formatDate(submitted.get().getStartTimestamp());
    } else {
      return "n/a";
    }
  }

  @Override
  public Optional<StatusUpdate> getMaxStatusUpdate(Collection<StatusUpdate> statusUpdates) {
    if (CollectionUtils.isNotEmpty(statusUpdates)) {
      StatusUpdate maxStatusUpdate = Collections.max(statusUpdates, Comparator.comparing(StatusUpdate::getStartTimestamp, Comparator.nullsFirst(Comparator.naturalOrder())));
      return Optional.ofNullable(maxStatusUpdate);
    } else {
      return Optional.empty();
    }
  }

}

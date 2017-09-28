package components.service;

import com.google.inject.Inject;
import components.dao.StatusUpdateDao;
import models.enums.StatusType;

public class ApplicationServiceImpl implements ApplicationService {

  private final StatusUpdateDao statusUpdateDao;

  @Inject
  public ApplicationServiceImpl(StatusUpdateDao statusUpdateDao) {
    this.statusUpdateDao = statusUpdateDao;
  }

  @Override
  public boolean isApplicationInProgress(String appId) {
    return statusUpdateDao.getStatusUpdates(appId).stream()
        .noneMatch(statusUpdate -> statusUpdate.getStatusType() == StatusType.COMPLETE);
  }

}

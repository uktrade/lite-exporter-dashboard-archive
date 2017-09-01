package components.service;

import com.google.inject.Inject;
import components.dao.StatusUpdateDao;
import models.Application;
import models.StatusUpdate;
import models.enums.StatusType;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

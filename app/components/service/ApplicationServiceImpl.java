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

  private static final List<StatusType> INVERSE_STATUS_TYPE_LIST;

  private final StatusUpdateDao statusUpdateDao;

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

  @Inject
  public ApplicationServiceImpl(StatusUpdateDao statusUpdateDao) {
    this.statusUpdateDao = statusUpdateDao;
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

  @Override
  public boolean isApplicationInProgress(String appId) {
    return statusUpdateDao.getStatusUpdates(appId).stream()
        .noneMatch(statusUpdate -> statusUpdate.getStatusType() == StatusType.COMPLETE);
  }

}

package components.dao.impl;

import com.google.inject.Inject;
import components.dao.StatusUpdateDao;
import components.dao.jdbi.StatusUpdateJDBIDao;
import java.util.ArrayList;
import java.util.List;
import models.StatusUpdate;
import org.skife.jdbi.v2.DBI;

public class StatusUpdateDaoImpl implements StatusUpdateDao {

  private final StatusUpdateJDBIDao statusUpdateJDBIDao;

  @Inject
  public StatusUpdateDaoImpl(DBI dbi) {
    this.statusUpdateJDBIDao = dbi.onDemand(StatusUpdateJDBIDao.class);
  }

  @Override
  public List<StatusUpdate> getStatusUpdates(String appId) {
    return statusUpdateJDBIDao.getStatusUpdates(appId);
  }

  @Override
  public List<StatusUpdate> getStatusUpdates(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      return statusUpdateJDBIDao.getStatusUpdates(appIds);
    }
  }

  @Override
  public void insertStatusUpdate(StatusUpdate statusUpdate) {
    statusUpdateJDBIDao.insert(statusUpdate.getId(),
        statusUpdate.getAppId(),
        statusUpdate.getStatusType().toString(),
        statusUpdate.getCreatedTimestamp());
  }

  @Override
  public void deleteAllStatusUpdates() {
    statusUpdateJDBIDao.truncateTable();
  }

  @Override
  public void deleteStatusUpdatesByAppId(String appId) {
    statusUpdateJDBIDao.deleteStatusUpdatesByAppId(appId);
  }

}

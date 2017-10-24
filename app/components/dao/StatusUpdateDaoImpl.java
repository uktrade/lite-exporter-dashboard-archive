package components.dao;

import com.google.inject.Inject;
import components.exceptions.DatabaseException;
import java.util.ArrayList;
import java.util.List;
import models.Application;
import models.StatusUpdate;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class StatusUpdateDaoImpl implements StatusUpdateDao {

  private final DBI dbi;

  @Inject
  public StatusUpdateDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<StatusUpdate> getStatusUpdates(String appId) {
    try (final Handle handle = dbi.open()) {
      StatusUpdateJDBIDao statusUpdateJDBIDao = handle.attach(StatusUpdateJDBIDao.class);
      return statusUpdateJDBIDao.getStatusUpdates(appId);
    }
  }

  @Override
  public List<StatusUpdate> getStatusUpdates(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      try (final Handle handle = dbi.open()) {
        StatusUpdateJDBIDao statusUpdateJDBIDao = handle.attach(StatusUpdateJDBIDao.class);
        return statusUpdateJDBIDao.getStatusUpdates(appIds);
      }
    }
  }

  @Override
  public void insertStatusUpdate(StatusUpdate statusUpdate) {
    try (final Handle handle = dbi.open()) {
      StatusUpdateJDBIDao statusUpdateJDBIDao = handle.attach(StatusUpdateJDBIDao.class);
      statusUpdateJDBIDao.insert(statusUpdate.getId(),
          statusUpdate.getAppId(),
          statusUpdate.getStatusType().toString(),
          statusUpdate.getCreatedTimestamp());
    }
  }

  @Override
  public void deleteAllStatusUpdates() {
    try (final Handle handle = dbi.open()) {
      StatusUpdateJDBIDao statusUpdateJDBIDao = handle.attach(StatusUpdateJDBIDao.class);
      statusUpdateJDBIDao.truncateTable();
    }
  }

  @Override
  public void deleteStatusUpdatesByAppId(String appId) {
    try (final Handle handle = dbi.open()) {
      StatusUpdateJDBIDao statusUpdateJDBIDao = handle.attach(StatusUpdateJDBIDao.class);
      statusUpdateJDBIDao.deleteStatusUpdatesByAppId(appId);
    }
  }

}

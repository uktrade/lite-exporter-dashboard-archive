package components.dao;

import com.google.inject.Inject;
import components.exceptions.DatabaseException;
import models.Application;
import models.StatusUpdate;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.ArrayList;
import java.util.List;

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
      ApplicationJDBIDao applicationJDBIDao = handle.attach(ApplicationJDBIDao.class);
      handle.useTransaction((conn, status) -> {
        Application application = applicationJDBIDao.getApplication(statusUpdate.getAppId());
        if (application == null) {
          String message = "Unable to insert statusUpdate since no application exists with appId" + statusUpdate.getAppId();
          throw new DatabaseException(message);
        }
        StatusUpdate existingStatusUpdate = statusUpdateJDBIDao.getStatusUpdate(statusUpdate.getAppId(), statusUpdate.getStatusType().toString());
        if (existingStatusUpdate != null) {
          String message = String.format("Unable to insert statusUpdate since a statusUpdate with the same appId %s and status %s already exists",
              statusUpdate.getAppId(), statusUpdate.getStatusType());
          throw new DatabaseException(message);
        }
        statusUpdateJDBIDao.insert(statusUpdate.getAppId(), statusUpdate.getStatusType().toString(), statusUpdate.getStartTimestamp(), statusUpdate.getEndTimestamp());
      });
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

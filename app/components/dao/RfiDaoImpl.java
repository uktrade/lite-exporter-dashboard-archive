package components.dao;

import com.google.inject.Inject;
import components.exceptions.DatabaseException;
import models.Application;
import models.Rfi;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.ArrayList;
import java.util.List;

public class RfiDaoImpl implements RfiDao {

  private final DBI dbi;

  @Inject
  public RfiDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<Rfi> getRfiList(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      try (final Handle handle = dbi.open()) {
        RfiJDBIDao rfiJDBIDao = handle.attach(RfiJDBIDao.class);
        return rfiJDBIDao.getRfiList(appIds);
      }
    }
  }

  @Override
  public List<Rfi> getRfiList(String appId) {
    try (final Handle handle = dbi.open()) {
      RfiJDBIDao rfiJDBIDao = handle.attach(RfiJDBIDao.class);
      return rfiJDBIDao.getRfiList(appId);
    }
  }

  @Override
  public int getRfiCount(String appId) {
    try (final Handle handle = dbi.open()) {
      RfiJDBIDao rfiJDBIDao = handle.attach(RfiJDBIDao.class);
      return rfiJDBIDao.getRfiCount(appId);
    }
  }

  @Override
  public void insertRfi(Rfi rfi) {
    try (final Handle handle = dbi.open()) {
      RfiJDBIDao rfiJDBIDao = handle.attach(RfiJDBIDao.class);
      ApplicationJDBIDao applicationJDBIDao = handle.attach(ApplicationJDBIDao.class);
      handle.useTransaction((conn, status) -> {
        Application application = applicationJDBIDao.getApplication(rfi.getAppId());
        if (application == null) {
          String message = "Unable to insert rfi since no application exists with appId " + rfi.getAppId();
          throw new DatabaseException(message);
        }
        rfiJDBIDao.insert(rfi.getRfiId(), rfi.getAppId(), rfi.getRfiStatus(), rfi.getReceivedTimestamp(), rfi.getDueTimestamp(), rfi.getSentBy(), rfi.getMessage());
      });
    }
  }

  @Override
  public void deleteAllRfiData() {
    try (final Handle handle = dbi.open()) {
      RfiJDBIDao rfiJDBIDao = handle.attach(RfiJDBIDao.class);
      rfiJDBIDao.truncateTable();
    }
  }

  @Override
  public void deleteRfiListByAppId(String appId) {
    try (final Handle handle = dbi.open()) {
      RfiJDBIDao rfiJDBIDao = handle.attach(RfiJDBIDao.class);
      rfiJDBIDao.deleteRfiListByAppId(appId);
    }
  }

}

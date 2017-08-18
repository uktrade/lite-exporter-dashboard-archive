package components.dao;

import com.google.inject.Inject;
import models.Rfi;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;

public class RfiDaoImpl implements RfiDao {

  private final DBI dbi;

  @Inject
  public RfiDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<Rfi> getRfiList(List<String> appIds) {
    try (final Handle handle = dbi.open()) {
      RfiJDBIDao rfiJDBIDao = handle.attach(RfiJDBIDao.class);
      return rfiJDBIDao.getRfiList(appIds);
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
      rfiJDBIDao.insert(rfi.getRfiId(), rfi.getAppId(), rfi.getRfiStatus(), rfi.getReceivedTimestamp(), rfi.getDueTimestamp(), rfi.getSentBy(), rfi.getMessage());
    }
  }

  @Override
  public void deleteAllRfiData() {
    try (final Handle handle = dbi.open()) {
      RfiJDBIDao rfiJDBIDao = handle.attach(RfiJDBIDao.class);
      rfiJDBIDao.truncateTable();
    }
  }

}

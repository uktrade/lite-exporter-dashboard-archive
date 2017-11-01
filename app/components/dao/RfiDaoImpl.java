package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import models.Rfi;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

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
  public void insertRfi(Rfi rfi) {
    try (final Handle handle = dbi.open()) {
      RfiJDBIDao rfiJDBIDao = handle.attach(RfiJDBIDao.class);
      rfiJDBIDao.insert(rfi.getId(),
          rfi.getAppId(),
          rfi.getCreatedTimestamp(),
          rfi.getDueTimestamp(),
          rfi.getCreatedByUserId(),
          JsonUtil.convertListToJson(rfi.getRecipientUserIds()),
          rfi.getMessage());
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

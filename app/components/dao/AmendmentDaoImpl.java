package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import models.Amendment;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;

public class AmendmentDaoImpl implements AmendmentDao {

  private final DBI dbi;

  @Inject
  public AmendmentDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<Amendment> getAmendments(String appId) {
    try (final Handle handle = dbi.open()) {
      AmendmentJDBIDao amendmentJDBIDao = handle.attach(AmendmentJDBIDao.class);
      return amendmentJDBIDao.getAmendments(appId);
    }
  }

  @Override
  public void insertAmendment(Amendment amendment) {
    try (final Handle handle = dbi.open()) {
      AmendmentJDBIDao amendmentJDBIDao = handle.attach(AmendmentJDBIDao.class);
      amendmentJDBIDao.insertAmendment(amendment.getAmendmentId(),
          amendment.getAppId(),
          amendment.getSentTimestamp(),
          amendment.getSentBy(),
          amendment.getMessage(),
          JsonUtil.convertFilesToJson(amendment.getAttachments()));
    }
  }

  @Override
  public void deleteAllAmendments() {
    try (final Handle handle = dbi.open()) {
      AmendmentJDBIDao amendmentJDBIDao = handle.attach(AmendmentJDBIDao.class);
      amendmentJDBIDao.truncateTable();
    }
  }

  @Override
  public void deleteAmendmentsByAppIds(List<String> appIds) {
    if (!appIds.isEmpty()) {
      try (final Handle handle = dbi.open()) {
        AmendmentJDBIDao amendmentJDBIDao = handle.attach(AmendmentJDBIDao.class);
        amendmentJDBIDao.deleteAmendmentsByAppIds(appIds);
      }
    }
  }

}

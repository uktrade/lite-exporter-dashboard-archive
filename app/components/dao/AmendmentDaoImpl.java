package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.Amendment;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class AmendmentDaoImpl implements AmendmentDao {

  private final DBI dbi;

  @Inject
  public AmendmentDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<Amendment> getAmendments(String appId) {
    return getAmendments(Collections.singletonList(appId));
  }

  @Override
  public List<Amendment> getAmendments(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      try (final Handle handle = dbi.open()) {
        AmendmentJDBIDao amendmentJDBIDao = handle.attach(AmendmentJDBIDao.class);
        return amendmentJDBIDao.getAmendments(appIds);
      }
    }
  }

  @Override
  public void insertAmendment(Amendment amendment) {
    try (final Handle handle = dbi.open()) {
      AmendmentJDBIDao amendmentJDBIDao = handle.attach(AmendmentJDBIDao.class);
      amendmentJDBIDao.insertAmendment(amendment.getId(),
          amendment.getAppId(),
          amendment.getCreatedByUserId(),
          amendment.getCreatedTimestamp(),
          amendment.getMessage(),
          JsonUtil.convertListToJson(amendment.getAttachments()));
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
  public void deleteAmendmentsByAppId(String appId) {
    try (final Handle handle = dbi.open()) {
      AmendmentJDBIDao amendmentJDBIDao = handle.attach(AmendmentJDBIDao.class);
      amendmentJDBIDao.deleteAmendmentsByAppId(appId);
    }
  }

}

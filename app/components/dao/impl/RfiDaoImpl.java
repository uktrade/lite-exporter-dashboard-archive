package components.dao.impl;

import com.google.inject.Inject;
import components.dao.RfiDao;
import components.dao.jdbi.RfiJDBIDao;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import models.Rfi;
import org.skife.jdbi.v2.DBI;

public class RfiDaoImpl implements RfiDao {

  private final RfiJDBIDao rfiJDBIDao;

  @Inject
  public RfiDaoImpl(DBI dbi) {
    this.rfiJDBIDao = dbi.onDemand(RfiJDBIDao.class);
  }

  @Override
  public List<Rfi> getRfiList(List<String> caseReferences) {
    if (caseReferences.isEmpty()) {
      return new ArrayList<>();
    } else {
      return rfiJDBIDao.getRfiList(caseReferences);
    }
  }

  @Override
  public void insertRfi(Rfi rfi) {
    rfiJDBIDao.insert(rfi.getId(),
        rfi.getCaseReference(),
        rfi.getCreatedTimestamp(),
        rfi.getDueTimestamp(),
        rfi.getCreatedByUserId(),
        JsonUtil.convertListToJson(rfi.getRecipientUserIds()),
        rfi.getMessage());
  }

  @Override
  public void deleteAllRfiData() {
    rfiJDBIDao.truncateTable();
  }

  @Override
  public void deleteRfiByCaseReference(String caseReference) {
    rfiJDBIDao.deleteRfiByCaseReference(caseReference);
  }

}

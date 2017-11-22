package components.dao.impl;

import com.google.inject.Inject;
import components.dao.RfiWithdrawalDao;
import components.dao.jdbi.RfiWithdrawalJDBIDao;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import models.RfiWithdrawal;
import org.skife.jdbi.v2.DBI;

public class RfiWithdrawalDaoImpl implements RfiWithdrawalDao {

  private final RfiWithdrawalJDBIDao rfiWithdrawalJDBIDao;

  @Inject
  public RfiWithdrawalDaoImpl(DBI dbi) {
    this.rfiWithdrawalJDBIDao = dbi.onDemand(RfiWithdrawalJDBIDao.class);
  }

  @Override
  public List<RfiWithdrawal> getRfiWithdrawals(List<String> rfiIds) {
    if (rfiIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      return rfiWithdrawalJDBIDao.getRfiWithdrawals(rfiIds);
    }
  }

  @Override
  public void insertRfiWithdrawal(RfiWithdrawal rfiWithdrawal) {
    rfiWithdrawalJDBIDao.insertRfiWithdrawal(rfiWithdrawal.getId(),
        rfiWithdrawal.getRfiId(),
        rfiWithdrawal.getCreatedByUserId(),
        rfiWithdrawal.getCreatedTimestamp(),
        JsonUtil.convertListToJson(rfiWithdrawal.getRecipientUserIds()),
        rfiWithdrawal.getMessage());
  }

  @Override
  public void deleteAllRfiWithdrawals() {
    rfiWithdrawalJDBIDao.truncateTable();
  }

  @Override
  public void deleteRfiWithdrawalByRfiId(String rfiId) {
    rfiWithdrawalJDBIDao.deleteRfiWithdrawalByRfiId(rfiId);
  }

}

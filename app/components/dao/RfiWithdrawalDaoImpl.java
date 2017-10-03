package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import models.RfiWithdrawal;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.ArrayList;
import java.util.List;

public class RfiWithdrawalDaoImpl implements RfiWithdrawalDao {

  private final DBI dbi;

  @Inject
  public RfiWithdrawalDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<RfiWithdrawal> getRfiWithdrawals(List<String> rfiIds) {
    if (rfiIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      try (Handle handle = dbi.open()) {
        RfiWithdrawalJDBIDao rfiWithdrawalJDBIDao = handle.attach(RfiWithdrawalJDBIDao.class);
        return rfiWithdrawalJDBIDao.getRfiWithdrawals(rfiIds);
      }
    }
  }

  @Override
  public void insertRfiWithdrawal(RfiWithdrawal rfiWithdrawal) {
    try (Handle handle = dbi.open()) {
      RfiWithdrawalJDBIDao rfiWithdrawalJDBIDao = handle.attach(RfiWithdrawalJDBIDao.class);
      rfiWithdrawalJDBIDao.insertRfiWithdrawal(rfiWithdrawal.getId(),
          rfiWithdrawal.getRfiId(),
          rfiWithdrawal.getCreatedByUserId(),
          rfiWithdrawal.getCreatedTimestamp(),
          JsonUtil.convertListToJson(rfiWithdrawal.getRecipientUserIds()),
          rfiWithdrawal.getMessage());
    }
  }

  @Override
  public void deleteAllRfiWithdrawals() {
    try (Handle handle = dbi.open()) {
      RfiWithdrawalJDBIDao rfiWithdrawalJDBIDao = handle.attach(RfiWithdrawalJDBIDao.class);
      rfiWithdrawalJDBIDao.truncateTable();
    }
  }

  @Override
  public void deleteRfiWithdrawalByRfiId(String rfiId) {
    try (Handle handle = dbi.open()) {
      RfiWithdrawalJDBIDao rfiWithdrawalJDBIDao = handle.attach(RfiWithdrawalJDBIDao.class);
      rfiWithdrawalJDBIDao.deleteRfiWithdrawalByRfiId(rfiId);
    }
  }

}

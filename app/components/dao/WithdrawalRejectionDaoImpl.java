package components.dao;

import com.google.inject.Inject;
import models.WithdrawalRejection;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;

public class WithdrawalRejectionDaoImpl implements WithdrawalRejectionDao {

  private final DBI dbi;

  @Inject
  public WithdrawalRejectionDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<WithdrawalRejection> getWithdrawalRejections(String appId) {
    try (Handle handle = dbi.open()) {
      WithdrawalRejectionJDBIDao withdrawalRejectionJDBIDao = handle.attach(WithdrawalRejectionJDBIDao.class);
      return withdrawalRejectionJDBIDao.getWithdrawalRejections(appId);
    }
  }

  @Override
  public void insertWithdrawalRejection(WithdrawalRejection withdrawalRejection) {
    try (Handle handle = dbi.open()) {
      WithdrawalRejectionJDBIDao withdrawalRejectionJDBIDao = handle.attach(WithdrawalRejectionJDBIDao.class);
      withdrawalRejectionJDBIDao.insertWithdrawalRejection(withdrawalRejection.getId(),
          withdrawalRejection.getAppId(),
          withdrawalRejection.getCreatedByUserId(),
          withdrawalRejection.getCreatedTimestamp(),
          withdrawalRejection.getMessage());
    }
  }

  @Override
  public void deleteWithdrawalRejectionsByAppId(String appId) {
    try (Handle handle = dbi.open()) {
      WithdrawalRejectionJDBIDao withdrawalRejectionJDBIDao = handle.attach(WithdrawalRejectionJDBIDao.class);
      withdrawalRejectionJDBIDao.deleteWithdrawalRejectionsByAppId(appId);
    }
  }

  @Override
  public void deleteAllWithdrawalRejections() {
    try (Handle handle = dbi.open()) {
      WithdrawalRejectionJDBIDao withdrawalRejectionJDBIDao = handle.attach(WithdrawalRejectionJDBIDao.class);
      withdrawalRejectionJDBIDao.truncateTable();
    }
  }

}

package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import models.WithdrawalRejection;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class WithdrawalRejectionDaoImpl implements WithdrawalRejectionDao {

  private final DBI dbi;

  @Inject
  public WithdrawalRejectionDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<WithdrawalRejection> getWithdrawalRejectionsByAppId(String appId) {
    try (Handle handle = dbi.open()) {
      WithdrawalRejectionJDBIDao withdrawalRejectionJDBIDao = handle.attach(WithdrawalRejectionJDBIDao.class);
      return withdrawalRejectionJDBIDao.getWithdrawalRejectionsByAppId(appId);
    }
  }

  @Override
  public List<WithdrawalRejection> getWithdrawalRejectionsByAppIds(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      try (Handle handle = dbi.open()) {
        WithdrawalRejectionJDBIDao withdrawalRejectionJDBIDao = handle.attach(WithdrawalRejectionJDBIDao.class);
        return withdrawalRejectionJDBIDao.getWithdrawalRejectionsByAppIds(appIds);
      }
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
          JsonUtil.convertListToJson(withdrawalRejection.getRecipientUserIds()),
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

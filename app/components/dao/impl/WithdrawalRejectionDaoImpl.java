package components.dao.impl;

import com.google.inject.Inject;
import components.dao.WithdrawalRejectionDao;
import components.dao.jdbi.WithdrawalRejectionJDBIDao;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import models.WithdrawalRejection;
import org.skife.jdbi.v2.DBI;

public class WithdrawalRejectionDaoImpl implements WithdrawalRejectionDao {

  private final WithdrawalRejectionJDBIDao withdrawalRejectionJDBIDao;

  @Inject
  public WithdrawalRejectionDaoImpl(DBI dbi) {
    this.withdrawalRejectionJDBIDao = dbi.onDemand(WithdrawalRejectionJDBIDao.class);
  }

  @Override
  public List<WithdrawalRejection> getWithdrawalRejectionsByAppId(String appId) {
    return withdrawalRejectionJDBIDao.getWithdrawalRejectionsByAppId(appId);
  }

  @Override
  public List<WithdrawalRejection> getWithdrawalRejectionsByAppIds(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      return withdrawalRejectionJDBIDao.getWithdrawalRejectionsByAppIds(appIds);
    }
  }

  @Override
  public void insertWithdrawalRejection(WithdrawalRejection withdrawalRejection) {
    withdrawalRejectionJDBIDao.insertWithdrawalRejection(withdrawalRejection.getId(),
        withdrawalRejection.getAppId(),
        withdrawalRejection.getCreatedByUserId(),
        withdrawalRejection.getCreatedTimestamp(),
        JsonUtil.convertListToJson(withdrawalRejection.getRecipientUserIds()),
        withdrawalRejection.getMessage());
  }

  @Override
  public void deleteWithdrawalRejectionsByAppId(String appId) {
    withdrawalRejectionJDBIDao.deleteWithdrawalRejectionsByAppId(appId);
  }

  @Override
  public void deleteAllWithdrawalRejections() {
    withdrawalRejectionJDBIDao.truncateTable();
  }

}

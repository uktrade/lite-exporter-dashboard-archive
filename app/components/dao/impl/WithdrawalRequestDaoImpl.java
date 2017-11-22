package components.dao.impl;

import com.google.inject.Inject;
import components.dao.WithdrawalRequestDao;
import components.dao.jdbi.WithdrawalRequestJDBIDao;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import models.WithdrawalRequest;
import org.skife.jdbi.v2.DBI;

public class WithdrawalRequestDaoImpl implements WithdrawalRequestDao {

  private final WithdrawalRequestJDBIDao withdrawalRequestJDBIDao;

  @Inject
  public WithdrawalRequestDaoImpl(DBI dbi) {
    this.withdrawalRequestJDBIDao = dbi.onDemand(WithdrawalRequestJDBIDao.class);
  }

  @Override
  public List<WithdrawalRequest> getWithdrawalRequestsByAppId(String appId) {
    return withdrawalRequestJDBIDao.getWithdrawalRequestsByAppId(appId);
  }

  @Override
  public List<WithdrawalRequest> getWithdrawalRequestsByAppIds(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      return withdrawalRequestJDBIDao.getWithdrawalRequestsByAppIds(appIds);
    }
  }

  @Override
  public void insertWithdrawalRequest(WithdrawalRequest withdrawalRequest) {
    withdrawalRequestJDBIDao.insert(withdrawalRequest.getId(),
        withdrawalRequest.getAppId(),
        withdrawalRequest.getCreatedByUserId(),
        withdrawalRequest.getCreatedTimestamp(),
        withdrawalRequest.getMessage(),
        JsonUtil.convertListToJson(withdrawalRequest.getAttachments()));
  }

  @Override
  public void deleteAllWithdrawalRequests() {
    withdrawalRequestJDBIDao.truncateTable();
  }

  @Override
  public void deleteWithdrawalRequestsByAppId(String appId) {
    withdrawalRequestJDBIDao.deleteWithdrawalRequestsByAppId(appId);
  }

}

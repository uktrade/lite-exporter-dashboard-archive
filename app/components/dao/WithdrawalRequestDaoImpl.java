package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import models.WithdrawalRequest;

import java.util.ArrayList;
import java.util.List;

public class WithdrawalRequestDaoImpl implements WithdrawalRequestDao {

  private final DBI dbi;

  @Inject
  public WithdrawalRequestDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<WithdrawalRequest> getWithdrawalRequestsByAppId(String appId) {
    try (final Handle handle = dbi.open()) {
      WithdrawalRequestJDBIDao withdrawalRequestJDBIDao = handle.attach(WithdrawalRequestJDBIDao.class);
      return withdrawalRequestJDBIDao.getWithdrawalRequestsByAppId(appId);
    }
  }

  @Override
  public List<WithdrawalRequest> getWithdrawalRequestsByAppIds(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      try (final Handle handle = dbi.open()) {
        WithdrawalRequestJDBIDao withdrawalRequestJDBIDao = handle.attach(WithdrawalRequestJDBIDao.class);
        return withdrawalRequestJDBIDao.getWithdrawalRequestsByAppIds(appIds);
      }
    }
  }

  @Override
  public void insertWithdrawalRequest(WithdrawalRequest withdrawalRequest) {
    try (final Handle handle = dbi.open()) {
      WithdrawalRequestJDBIDao withdrawalRequestJDBIDao = handle.attach(WithdrawalRequestJDBIDao.class);
      withdrawalRequestJDBIDao.insert(withdrawalRequest.getId(),
          withdrawalRequest.getAppId(),
          withdrawalRequest.getCreatedByUserId(),
          withdrawalRequest.getCreatedTimestamp(),
          withdrawalRequest.getMessage(),
          JsonUtil.convertListToJson(withdrawalRequest.getAttachments()));
    }
  }

  @Override
  public void deleteAllWithdrawalRequests() {
    try (final Handle handle = dbi.open()) {
      WithdrawalRequestJDBIDao withdrawalRequestJDBIDao = handle.attach(WithdrawalRequestJDBIDao.class);
      withdrawalRequestJDBIDao.truncateTable();
    }
  }

  @Override
  public void deleteWithdrawalRequestsByAppId(String appId) {
    try (final Handle handle = dbi.open()) {
      WithdrawalRequestJDBIDao withdrawalRequestJDBIDao = handle.attach(WithdrawalRequestJDBIDao.class);
      withdrawalRequestJDBIDao.deleteWithdrawalRequestsByAppId(appId);
    }
  }

}

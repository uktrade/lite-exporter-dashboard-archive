package components.dao;

import com.google.inject.Inject;
import models.WithdrawalRequest;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class WithdrawalRequestDaoImpl implements WithdrawalRequestDao {

  private final DBI dbi;

  @Inject
  public WithdrawalRequestDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public WithdrawalRequest getWithdrawalRequest(String appId) {
    try (final Handle handle = dbi.open()) {
      WithdrawalRequestJDBIDao withdrawalRequestJDBIDao = handle.attach(WithdrawalRequestJDBIDao.class);
      return withdrawalRequestJDBIDao.getWithdrawalRequest(appId);
    }
  }

  @Override
  public void insertWithdrawalRequest(WithdrawalRequest withdrawalRequest) {
    try (final Handle handle = dbi.open()) {
      WithdrawalRequestJDBIDao withdrawalRequestJDBIDao = handle.attach(WithdrawalRequestJDBIDao.class);
      withdrawalRequestJDBIDao.insert(withdrawalRequest.getWithdrawalRequestId(),
          withdrawalRequest.getAppId(),
          withdrawalRequest.getSentTimestamp(),
          withdrawalRequest.getSentBy(),
          withdrawalRequest.getMessage(),
          withdrawalRequest.getAttachments(),
          withdrawalRequest.getRejectedBy(),
          withdrawalRequest.getRejectedTimestamp(),
          withdrawalRequest.getRejectedMessage());
    }
  }

  @Override
  public void deleteAllWithdrawalRequests() {
    try (final Handle handle = dbi.open()) {
      WithdrawalRequestJDBIDao withdrawalRequestJDBIDao = handle.attach(WithdrawalRequestJDBIDao.class);
      withdrawalRequestJDBIDao.truncateTable();
    }
  }

}

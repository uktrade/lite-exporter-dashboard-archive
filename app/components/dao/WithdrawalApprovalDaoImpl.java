package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import models.WithdrawalApproval;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class WithdrawalApprovalDaoImpl implements WithdrawalApprovalDao {

  private final DBI dbi;

  @Inject
  public WithdrawalApprovalDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public WithdrawalApproval getWithdrawalApproval(String appId) {
    try (Handle handle = dbi.open()) {
      WithdrawalApprovalJDBIDao withdrawalApprovalJDBIDao = handle.attach(WithdrawalApprovalJDBIDao.class);
      return withdrawalApprovalJDBIDao.getWithdrawalApproval(appId);
    }
  }

  @Override
  public List<WithdrawalApproval> getWithdrawalApprovals(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      try (Handle handle = dbi.open()) {
        WithdrawalApprovalJDBIDao withdrawalApprovalJDBIDao = handle.attach(WithdrawalApprovalJDBIDao.class);
        return withdrawalApprovalJDBIDao.getWithdrawalApprovals(appIds);
      }
    }
  }

  @Override
  public void insertWithdrawalApproval(WithdrawalApproval withdrawalApproval) {
    try (Handle handle = dbi.open()) {
      WithdrawalApprovalJDBIDao withdrawalApprovalJDBIDao = handle.attach(WithdrawalApprovalJDBIDao.class);
      withdrawalApprovalJDBIDao.insertWithdrawalApproval(withdrawalApproval.getId(),
          withdrawalApproval.getAppId(),
          withdrawalApproval.getCreatedByUserId(),
          withdrawalApproval.getCreatedTimestamp(),
          JsonUtil.convertListToJson(withdrawalApproval.getRecipientUserIds()),
          withdrawalApproval.getMessage());
    }
  }

  @Override
  public void deleteWithdrawalApprovalsByAppId(String appId) {
    try (Handle handle = dbi.open()) {
      WithdrawalApprovalJDBIDao withdrawalApprovalJDBIDao = handle.attach(WithdrawalApprovalJDBIDao.class);
      withdrawalApprovalJDBIDao.deleteWithdrawalApprovalsByAppId(appId);
    }
  }

  @Override
  public void deleteAllWithdrawalApprovals() {
    try (Handle handle = dbi.open()) {
      WithdrawalApprovalJDBIDao withdrawalApprovalJDBIDao = handle.attach(WithdrawalApprovalJDBIDao.class);
      withdrawalApprovalJDBIDao.truncateTable();
    }
  }

}

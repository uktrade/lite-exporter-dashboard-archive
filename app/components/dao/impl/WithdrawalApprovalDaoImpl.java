package components.dao.impl;

import com.google.inject.Inject;
import components.dao.WithdrawalApprovalDao;
import components.dao.jdbi.WithdrawalApprovalJDBIDao;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import models.WithdrawalApproval;
import org.skife.jdbi.v2.DBI;

public class WithdrawalApprovalDaoImpl implements WithdrawalApprovalDao {

  private final WithdrawalApprovalJDBIDao withdrawalApprovalJDBIDao;

  @Inject
  public WithdrawalApprovalDaoImpl(DBI dbi) {
    this.withdrawalApprovalJDBIDao = dbi.onDemand(WithdrawalApprovalJDBIDao.class);
  }

  @Override
  public WithdrawalApproval getWithdrawalApproval(String appId) {
    return withdrawalApprovalJDBIDao.getWithdrawalApproval(appId);
  }

  @Override
  public List<WithdrawalApproval> getWithdrawalApprovals(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      return withdrawalApprovalJDBIDao.getWithdrawalApprovals(appIds);
    }
  }

  @Override
  public void insertWithdrawalApproval(WithdrawalApproval withdrawalApproval) {
    withdrawalApprovalJDBIDao.insertWithdrawalApproval(withdrawalApproval.getId(),
        withdrawalApproval.getAppId(),
        withdrawalApproval.getCreatedByUserId(),
        withdrawalApproval.getCreatedTimestamp(),
        JsonUtil.convertListToJson(withdrawalApproval.getRecipientUserIds()),
        withdrawalApproval.getMessage());
  }

  @Override
  public void deleteWithdrawalApprovalsByAppId(String appId) {
    withdrawalApprovalJDBIDao.deleteWithdrawalApprovalsByAppId(appId);
  }

  @Override
  public void deleteAllWithdrawalApprovals() {
    withdrawalApprovalJDBIDao.truncateTable();
  }

}

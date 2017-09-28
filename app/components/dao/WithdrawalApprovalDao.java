package components.dao;

import models.WithdrawalApproval;

public interface WithdrawalApprovalDao {

  WithdrawalApproval getWithdrawalApproval(String appId);

  void insertWithdrawalApproval(WithdrawalApproval withdrawalApproval);

  void deleteWithdrawalApprovalsByAppId(String appId);

  void deleteAllWithdrawalApprovals();

}

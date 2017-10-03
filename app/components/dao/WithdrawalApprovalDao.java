package components.dao;

import models.WithdrawalApproval;

import java.util.List;

public interface WithdrawalApprovalDao {

  WithdrawalApproval getWithdrawalApproval(String appId);

  List<WithdrawalApproval> getWithdrawalApprovals(List<String> appIds);

  void insertWithdrawalApproval(WithdrawalApproval withdrawalApproval);

  void deleteWithdrawalApprovalsByAppId(String appId);

  void deleteAllWithdrawalApprovals();

}

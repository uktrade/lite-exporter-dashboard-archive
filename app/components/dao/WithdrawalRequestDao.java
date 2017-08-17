package components.dao;

import models.WithdrawalRequest;

public interface WithdrawalRequestDao {
  WithdrawalRequest getWithdrawalRequest(String appId);

  void insertWithdrawalRequest(WithdrawalRequest withdrawalRequest);

  void deleteAllWithdrawalRequests();
}

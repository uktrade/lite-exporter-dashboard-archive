package components.dao;

import models.WithdrawalRequest;

import java.util.List;

public interface WithdrawalRequestDao {

  List<WithdrawalRequest> getWithdrawalRequestsByAppId(String appId);

  List<WithdrawalRequest> getWithdrawalRequestsByAppIds(List<String> appIds);

  void insertWithdrawalRequest(WithdrawalRequest withdrawalRequest);

  void deleteAllWithdrawalRequests();

  void deleteWithdrawalRequestsByAppId(String appId);

}

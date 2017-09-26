package components.dao;

import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

import java.util.List;

public interface WithdrawalRequestDao {

  List<WithdrawalRequest> getWithdrawalRequests(String appId);

  void insertWithdrawalRequest(WithdrawalRequest withdrawalRequest);

  void deleteAllWithdrawalRequests();

  void deleteWithdrawalRequestsByAppId(String appId);

}

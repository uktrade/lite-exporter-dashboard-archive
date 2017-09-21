package components.dao;

import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

import java.util.List;

public interface WithdrawalRequestDao {

  WithdrawalRequest getWithdrawalRequest(String appId);

  void insertWithdrawalRequest(WithdrawalRequest withdrawalRequest);

  void deleteAllWithdrawalRequests();

  void deleteWithdrawalRequestsByAppIds(List<String> appIds);

}

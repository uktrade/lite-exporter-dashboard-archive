package components.service;

public interface WithdrawalRequestService {

  void insertWithdrawalRequest(String createdByUserId, String appId, String message);

}

package components.service;

public interface ReadMessageService {

  void sendOutcomeReadMessage(String userId, String appId, String outcomeId);

  void sendNotificationReadMessage(String userId, String appId, String notificationId);

  void sendWithdrawalRequestAcceptReadMessage(String userId, String appId, String notificationId);

  void sendRfiWithdrawalReadMessage(String userId, String appId, String rfiId);

}

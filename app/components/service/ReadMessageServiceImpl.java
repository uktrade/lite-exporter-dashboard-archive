package components.service;

import com.google.inject.Inject;
import components.message.MessagePublisher;
import uk.gov.bis.lite.exporterdashboard.api.NotificationReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.OutcomeReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.RfiWithdrawalReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequestAcceptReadMessage;

public class ReadMessageServiceImpl implements ReadMessageService {

  private final MessagePublisher messagePublisher;

  @Inject
  public ReadMessageServiceImpl(MessagePublisher messagePublisher) {
    this.messagePublisher = messagePublisher;
  }

  @Override
  public void sendOutcomeReadMessage(String userId, String appId, String outcomeId) {
    OutcomeReadMessage outcomeReadMessage = new OutcomeReadMessage();
    outcomeReadMessage.setOutcomeId(outcomeId);
    outcomeReadMessage.setAppId(appId);
    outcomeReadMessage.setCreatedByUserId(userId);
    messagePublisher.sendMessage(RoutingKey.OUTCOME_READ, outcomeReadMessage);
  }

  @Override
  public void sendNotificationReadMessage(String userId, String appId, String notificationId) {
    NotificationReadMessage notificationReadMessage = new NotificationReadMessage();
    notificationReadMessage.setAppId(appId);
    notificationReadMessage.setCreatedByUserId(userId);
    notificationReadMessage.setNotificationId(notificationId);
    messagePublisher.sendMessage(RoutingKey.NOTIFICATION_READ, notificationReadMessage);
  }

  @Override
  public void sendWithdrawalRequestAcceptReadMessage(String userId, String appId, String notificationId) {
    WithdrawalRequestAcceptReadMessage withdrawalRequestAcceptReadMessage = new WithdrawalRequestAcceptReadMessage();
    withdrawalRequestAcceptReadMessage.setNotificationId(notificationId);
    withdrawalRequestAcceptReadMessage.setAppId(appId);
    withdrawalRequestAcceptReadMessage.setCreatedByUserId(userId);
    messagePublisher.sendMessage(RoutingKey.WITHDRAWAL_REQUEST_ACCEPT_READ, withdrawalRequestAcceptReadMessage);
  }

  @Override
  public void sendRfiWithdrawalReadMessage(String userId, String appId, String rfiId) {
    RfiWithdrawalReadMessage rfiWithdrawalReadMessage = new RfiWithdrawalReadMessage();
    rfiWithdrawalReadMessage.setAppId(appId);
    rfiWithdrawalReadMessage.setRfiId(rfiId);
    rfiWithdrawalReadMessage.setCreatedByUserId(userId);
    messagePublisher.sendMessage(RoutingKey.RFI_WITHDRAWAL_READ, rfiWithdrawalReadMessage);
  }

}

package components.service;

import static components.util.RandomIdUtil.readId;

import com.google.inject.Inject;
import components.dao.ReadDao;
import components.message.MessagePublisher;
import components.util.ApplicationUtil;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import models.AppData;
import models.Outcome;
import models.Read;
import models.ReadData;
import models.RecipientMessage;
import models.RfiWithdrawal;
import models.WithdrawalApproval;
import models.enums.ReadType;
import uk.gov.bis.lite.exporterdashboard.api.NotificationReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.OutcomeReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.RfiWithdrawalReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequestAcceptReadMessage;

public class ReadDataServiceImpl implements ReadDataService {

  private final ReadDao readDao;
  private final MessagePublisher messagePublisher;

  @Inject
  public ReadDataServiceImpl(ReadDao readDao, MessagePublisher messagePublisher) {
    this.readDao = readDao;
    this.messagePublisher = messagePublisher;
  }

  @Override
  public Map<String, ReadData> getReadData(String userId, List<AppData> appDataList) {
    EnumMap<ReadType, HashSet<String>> readMap = new EnumMap<>(ReadType.class);
    for (ReadType readType : ReadType.values()) {
      readMap.put(readType, new HashSet<>());
    }

    readDao.getReadList(userId).forEach(read -> readMap.get(read.getReadType()).add(read.getRelatedId()));

    Map<String, ReadData> readDataMap = new HashMap<>();
    for (AppData appData : appDataList) {

      ReadData readData = new ReadData(getUnreadId(userId, appData.getDelayNotification(), readMap.get(ReadType.NOTIFICATION)),
          getUnreadId(userId, appData.getWithdrawalApproval(), readMap.get(ReadType.WITHDRAWAL_APPROVAL)),
          getUnreadIds(userId, ApplicationUtil.getAllStopNotifications(appData), readMap.get(ReadType.NOTIFICATION)),
          getUnreadIds(userId, ApplicationUtil.getAllInformNotifications(appData), readMap.get(ReadType.NOTIFICATION)),
          getUnreadIds(userId, ApplicationUtil.getAllOutcomes(appData), readMap.get(ReadType.OUTCOME)),
          getUnreadIds(userId, appData.getWithdrawalRejections(), readMap.get(ReadType.WITHDRAWAL_REJECTION)),
          getUnreadIds(userId, appData.getRfiWithdrawals(), readMap.get(ReadType.RFI_WITHDRAWAL)));

      readDataMap.put(appData.getApplication().getId(), readData);
    }

    return readDataMap;
  }

  private String getUnreadId(String userId, RecipientMessage recipientMessage, Set<String> readIds) {
    if (recipientMessage != null && recipientMessage.getRecipientUserIds().contains(userId) && !readIds.contains(recipientMessage.getId())) {
      return recipientMessage.getId();
    } else {
      return null;
    }
  }

  private Set<String> getUnreadIds(String userId, List<? extends RecipientMessage> recipientMessages, Set<String> readIds) {
    return recipientMessages.stream()
        .map(spireMessage -> getUnreadId(userId, spireMessage, readIds))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  @Override
  public ReadData getReadData(String userId, AppData appData) {
    Map<String, ReadData> readDataMap = getReadData(userId, Collections.singletonList(appData));
    return readDataMap.get(appData.getApplication().getId());
  }

  @Override
  public void updateRfiTabReadData(String userId, AppData appData, ReadData readData) {
    appData.getRfiWithdrawals().stream()
        .filter(rfiWithdrawal -> readData.getUnreadRfiWithdrawalIds().contains(rfiWithdrawal.getId()))
        .forEach(rfiWithdrawal -> {
          insertRead(rfiWithdrawal.getId(), ReadType.RFI_WITHDRAWAL, userId);
          sendRfiWithdrawalReadMessage(userId, appData.getApplication().getId(), rfiWithdrawal);
        });
  }

  @Override
  public void updateMessageTabReadData(String userId, AppData appData, ReadData readData) {
    String appId = appData.getApplication().getId();
    if (readData.getUnreadDelayNotificationId() != null) {
      insertRead(readData.getUnreadDelayNotificationId(), ReadType.NOTIFICATION, userId);
      sendNotificationReadMessage(userId, appId, readData.getUnreadDelayNotificationId());
    }
    readData.getUnreadStopNotificationIds().forEach(notificationId -> {
      insertRead(notificationId, ReadType.NOTIFICATION, userId);
      sendNotificationReadMessage(userId, appId, notificationId);
    });
    if (readData.getUnreadWithdrawalApprovalId() != null) {
      WithdrawalApproval withdrawalApproval = appData.getWithdrawalApproval();
      insertRead(withdrawalApproval.getId(), ReadType.WITHDRAWAL_APPROVAL, userId);
      sendWithdrawalRequestAcceptReadMessage(userId, withdrawalApproval.getAppId(), withdrawalApproval.getId());
    }
    readData.getUnreadWithdrawalRejectionIds().forEach(withdrawalRejectionId ->
        insertRead(withdrawalRejectionId, ReadType.WITHDRAWAL_REJECTION, userId));
  }

  @Override
  public void updateDocumentTabReadData(String userId, AppData appData, ReadData readData) {
    String appId = appData.getApplication().getId();

    ApplicationUtil.getAllOutcomes(appData)
        .stream()
        .filter(outcome -> readData.getUnreadOutcomeIds().contains(outcome.getId()))
        .forEach(outcome -> {
          insertRead(outcome.getId(), ReadType.OUTCOME, userId);
          sendOutcomeReadMessage(userId, appId, outcome);
        });

    readData.getUnreadInformNotificationIds().forEach(notificationId -> {
      insertRead(notificationId, ReadType.NOTIFICATION, userId);
      sendNotificationReadMessage(userId, appId, notificationId);
    });
  }

  private void insertRead(String relatedId, ReadType readType, String userId) {
    Read read = new Read(readId(), relatedId, readType, userId);
    readDao.insertRead(read);
  }

  private void sendOutcomeReadMessage(String userId, String appId, Outcome outcome) {
    OutcomeReadMessage outcomeReadMessage = new OutcomeReadMessage();
    outcomeReadMessage.setOutcomeId(outcome.getId());
    outcomeReadMessage.setAppId(appId);
    outcomeReadMessage.setCreatedByUserId(userId);
    messagePublisher.sendMessage(RoutingKey.OUTCOME_READ, outcomeReadMessage);
  }

  private void sendNotificationReadMessage(String userId, String appId, String notificationId) {
    NotificationReadMessage notificationReadMessage = new NotificationReadMessage();
    notificationReadMessage.setAppId(appId);
    notificationReadMessage.setCreatedByUserId(userId);
    notificationReadMessage.setNotificationId(notificationId);
    messagePublisher.sendMessage(RoutingKey.NOTIFICATION_READ, notificationReadMessage);
  }

  private void sendWithdrawalRequestAcceptReadMessage(String userId, String appId, String notificationId) {
    WithdrawalRequestAcceptReadMessage withdrawalRequestAcceptReadMessage = new WithdrawalRequestAcceptReadMessage();
    withdrawalRequestAcceptReadMessage.setNotificationId(notificationId);
    withdrawalRequestAcceptReadMessage.setAppId(appId);
    withdrawalRequestAcceptReadMessage.setCreatedByUserId(userId);
    messagePublisher.sendMessage(RoutingKey.WITHDRAWAL_REQUEST_ACCEPT_READ, withdrawalRequestAcceptReadMessage);
  }

  private void sendRfiWithdrawalReadMessage(String userId, String appId, RfiWithdrawal rfiWithdrawal) {
    RfiWithdrawalReadMessage rfiWithdrawalReadMessage = new RfiWithdrawalReadMessage();
    rfiWithdrawalReadMessage.setAppId(appId);
    rfiWithdrawalReadMessage.setRfiId(rfiWithdrawal.getRfiId());
    rfiWithdrawalReadMessage.setCreatedByUserId(userId);
    messagePublisher.sendMessage(RoutingKey.RFI_WITHDRAWAL_READ, rfiWithdrawalReadMessage);
  }

}

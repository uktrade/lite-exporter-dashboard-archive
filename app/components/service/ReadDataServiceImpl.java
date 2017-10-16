package components.service;

import static components.util.RandomIdUtil.readId;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.dao.ReadDao;
import components.message.MessagePublisher;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import models.AppData;
import models.Notification;
import models.Outcome;
import models.Read;
import models.ReadData;
import models.RfiWithdrawal;
import models.enums.ReadType;
import uk.gov.bis.lite.exporterdashboard.api.NotificationReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.OutcomeReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.RfiWithdrawalReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;

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
    List<Read> readList = readDao.getReadList(userId);

    HashSet<String> readNotificationIds = new HashSet<>();
    HashSet<String> readOutcomeIds = new HashSet<>();
    HashSet<String> readWithdrawalRejectionIds = new HashSet<>();
    HashSet<String> readRfiWithdrawalIds = new HashSet<>();
    HashSet<String> readWithdrawalApprovalIds = new HashSet<>();
    for (Read read : readList) {
      switch (read.getReadType()) {
      case OUTCOME:
        readOutcomeIds.add(read.getRelatedId());
        break;
      case WITHDRAWAL_REJECTION:
        readWithdrawalRejectionIds.add(read.getRelatedId());
        break;
      case WITHDRAWAL_APPROVAL:
        readWithdrawalApprovalIds.add(read.getRelatedId());
      case NOTIFICATION:
        readNotificationIds.add(read.getRelatedId());
        break;
      case RFI_WITHDRAWAL:
        readRfiWithdrawalIds.add(read.getRelatedId());
        break;
      }
    }

    Map<String, String> unreadDelayNotificationIds = new HashMap<>();
    appDataList.stream()
        .map(AppData::getDelayNotification)
        .filter(Objects::nonNull)
        .filter(notification -> notification.getRecipientUserIds().contains(userId))
        .filter(notification -> !readNotificationIds.contains(notification.getId()))
        .forEach(notification -> unreadDelayNotificationIds.put(notification.getAppId(), notification.getId()));

    Map<String, String> unreadStopNotificationIds = new HashMap<>();
    appDataList.stream()
        .map(AppData::getStopNotification)
        .filter(Objects::nonNull)
        .filter(notification -> notification.getRecipientUserIds().contains(userId))
        .filter(notification -> !readNotificationIds.contains(notification.getId()))
        .forEach(notification -> unreadStopNotificationIds.put(notification.getAppId(), notification.getId()));

    Map<String, String> unreadWithdrawalIds = new HashMap<>();
    appDataList.stream()
        .map(AppData::getWithdrawalApproval)
        .filter(Objects::nonNull)
        .filter(withdrawalApproval -> withdrawalApproval.getRecipientUserIds().contains(userId))
        .filter(withdrawalApproval -> !readWithdrawalApprovalIds.contains(withdrawalApproval.getId()))
        .forEach(withdrawalApproval -> unreadWithdrawalIds.put(withdrawalApproval.getAppId(), withdrawalApproval.getId()));

    Multimap<String, String> unreadInformNotificationIds = HashMultimap.create();
    appDataList.stream()
        .flatMap(appData -> appData.getInformNotifications().stream())
        .filter(notification -> notification.getRecipientUserIds().contains(userId))
        .filter(notification -> !readNotificationIds.contains(notification.getId()))
        .forEach(notification -> unreadInformNotificationIds.put(notification.getAppId(), notification.getId()));

    Multimap<String, String> unreadOutcomeIds = HashMultimap.create();
    appDataList.stream()
        .flatMap(appData -> appData.getOutcomes().stream())
        .filter(outcome -> outcome.getRecipientUserIds().contains(userId))
        .filter(outcome -> !readOutcomeIds.contains(outcome.getId()))
        .forEach(outcome -> unreadOutcomeIds.put(outcome.getAppId(), outcome.getId()));

    Multimap<String, String> unreadWithdrawalRejectionIdMultimap = HashMultimap.create();
    appDataList.stream()
        .flatMap(appData -> appData.getWithdrawalRejections().stream())
        .filter(withdrawalRejection -> withdrawalRejection.getRecipientUserIds().contains(userId))
        .filter(withdrawalRejection -> !readWithdrawalRejectionIds.contains(withdrawalRejection.getId()))
        .forEach(withdrawalRejection -> unreadWithdrawalRejectionIdMultimap.put(withdrawalRejection.getAppId(), withdrawalRejection.getId()));

    Multimap<String, String> unreadRfiWithdrawalIds = HashMultimap.create();
    appDataList.forEach(appData -> {
      appData.getRfiWithdrawals().stream()
          .filter(rfiWithdrawal -> rfiWithdrawal.getRecipientUserIds().contains(userId))
          .filter(rfiWithdrawal -> !readRfiWithdrawalIds.contains(rfiWithdrawal.getId()))
          .forEach(rfiWithdrawal -> {
            unreadRfiWithdrawalIds.put(appData.getApplication().getId(), rfiWithdrawal.getId());
          });
    });

    Map<String, ReadData> readDataMap = new HashMap<>();
    for (AppData appData : appDataList) {
      String appId = appData.getApplication().getId();
      ReadData readData = new ReadData(unreadDelayNotificationIds.get(appId),
          unreadStopNotificationIds.get(appId),
          unreadWithdrawalIds.get(appId),
          new HashSet<>(unreadInformNotificationIds.get(appId)),
          new HashSet<>(unreadOutcomeIds.get(appId)),
          new HashSet<>(unreadWithdrawalRejectionIdMultimap.get(appId)),
          new HashSet<>(unreadRfiWithdrawalIds.get(appId)));
      readDataMap.put(appId, readData);
    }
    return readDataMap;
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
    if (readData.getUnreadDelayNotificationId() != null) {
      Notification notification = appData.getDelayNotification();
      insertRead(notification.getId(), ReadType.NOTIFICATION, userId);
      sendNotificationReadMessage(userId, notification);
    }
    if (readData.getUnreadStopNotificationId() != null) {
      Notification notification = appData.getStopNotification();
      insertRead(notification.getId(), ReadType.NOTIFICATION, userId);
      sendNotificationReadMessage(userId, notification);
    }
    if (readData.getUnreadWithdrawalApprovalId() != null) {
      insertRead(readData.getUnreadWithdrawalApprovalId(), ReadType.WITHDRAWAL_APPROVAL, userId);
    }
    readData.getUnreadWithdrawalRejectionIds().forEach(withdrawalRejectionId ->
        insertRead(withdrawalRejectionId, ReadType.WITHDRAWAL_REJECTION, userId));
  }

  @Override
  public void updateDocumentTabReadData(String userId, AppData appData, ReadData readData) {
    appData.getOutcomes().stream()
        .filter(outcome -> readData.getUnreadOutcomeIds().contains(outcome.getId()))
        .forEach(outcome -> {
          insertRead(outcome.getId(), ReadType.OUTCOME, userId);
          sendOutcomeReadMessage(userId, outcome);
        });
    appData.getInformNotifications().stream()
        .filter(notification -> readData.getUnreadInformNotificationIds().contains(notification.getId()))
        .forEach(notification -> {
          insertRead(notification.getId(), ReadType.NOTIFICATION, userId);
          sendNotificationReadMessage(userId, notification);
        });
  }

  private void insertRead(String relatedId, ReadType readType, String userId) {
    Read read = new Read(readId(), relatedId, readType, userId);
    readDao.insertRead(read);
  }

  private void sendOutcomeReadMessage(String userId, Outcome outcome) {
    OutcomeReadMessage outcomeReadMessage = new OutcomeReadMessage();
    outcomeReadMessage.setOutcomeId(outcome.getId());
    outcomeReadMessage.setAppId(outcome.getAppId());
    outcomeReadMessage.setCreatedByUserId(userId);
    messagePublisher.sendMessage(RoutingKey.OUTCOME_READ, outcomeReadMessage);
  }

  private void sendNotificationReadMessage(String userId, Notification notification) {
    NotificationReadMessage notificationReadMessage = new NotificationReadMessage();
    notificationReadMessage.setAppId(notification.getAppId());
    notificationReadMessage.setCreatedByUserId(userId);
    notificationReadMessage.setNotificationId(notification.getId());
    messagePublisher.sendMessage(RoutingKey.NOTIFICATION_READ, notificationReadMessage);
  }

  private void sendRfiWithdrawalReadMessage(String userId, String appId, RfiWithdrawal rfiWithdrawal) {
    RfiWithdrawalReadMessage rfiWithdrawalReadMessage = new RfiWithdrawalReadMessage();
    rfiWithdrawalReadMessage.setAppId(appId);
    rfiWithdrawalReadMessage.setRfiId(rfiWithdrawal.getRfiId());
    rfiWithdrawalReadMessage.setCreatedByUserId(userId);
    messagePublisher.sendMessage(RoutingKey.RFI_WITHDRAWAL_READ, rfiWithdrawalReadMessage);
  }

}

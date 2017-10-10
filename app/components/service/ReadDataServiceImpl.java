package components.service;

import static components.util.RandomIdUtil.readId;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.dao.ReadDao;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import models.AppData;
import models.Read;
import models.ReadData;
import models.enums.ReadType;

public class ReadDataServiceImpl implements ReadDataService {

  private final ReadDao readDao;

  @Inject
  public ReadDataServiceImpl(ReadDao readDao) {
    this.readDao = readDao;
  }

  @Override
  public Map<String, ReadData> getReadData(String userId, List<AppData> appDataList) {
    List<Read> readList = readDao.getReadList(userId);

    HashSet<String> readNotificationIds = new HashSet<>();
    HashSet<String> readOutcomeIds = new HashSet<>();
    HashSet<String> readWithdrawalRejectionIds = new HashSet<>();
    for (Read read : readList) {
      switch (read.getReadType()) {
      case OUTCOME:
        readOutcomeIds.add(read.getRelatedId());
        break;
      case WITHDRAWAL_REJECTION:
        readWithdrawalRejectionIds.add(read.getRelatedId());
        break;
      case NOTIFICATION:
        readNotificationIds.add(read.getRelatedId());
        break;
      case RFI_WITHDRAWAL:
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

    Map<String, ReadData> readDataMap = new HashMap<>();
    for (AppData appData : appDataList) {
      String appId = appData.getApplication().getId();
      ReadData readData = new ReadData(unreadDelayNotificationIds.get(appId),
          unreadStopNotificationIds.get(appId),
          new HashSet<>(unreadInformNotificationIds.get(appId)),
          new HashSet<>(unreadOutcomeIds.get(appId)),
          new HashSet<>(unreadWithdrawalRejectionIdMultimap.get(appId)));
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
  public void updateMessageTabReadData(String userId, ReadData readData) {
    if (readData.getUnreadDelayNotificationId() != null) {
      insertRead(readData.getUnreadDelayNotificationId(), ReadType.NOTIFICATION, userId);
    }
    if (readData.getUnreadStopNotificationId() != null) {
      insertRead(readData.getUnreadStopNotificationId(), ReadType.NOTIFICATION, userId);
    }
    readData.getUnreadWithdrawalRejectionIds().forEach(withdrawalRejectionId -> insertRead(withdrawalRejectionId, ReadType.WITHDRAWAL_REJECTION, userId));
  }

  @Override
  public void updateDocumentTabReadData(String userId, ReadData readData) {
    readData.getUnreadOutcomeIds().forEach(outcomeId -> insertRead(outcomeId, ReadType.OUTCOME, userId));
    readData.getUnreadInformNotificationIds().forEach(notificationId -> insertRead(notificationId, ReadType.NOTIFICATION, userId));
  }

  private void insertRead(String relatedId, ReadType readType, String userId) {
    Read read = new Read(readId(), relatedId, readType, userId);
    readDao.insertRead(read);
  }

}

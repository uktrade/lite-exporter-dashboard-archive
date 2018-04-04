package components.service;

import static components.util.RandomIdUtil.readId;

import com.google.inject.Inject;
import components.dao.ReadDao;
import components.util.ApplicationUtil;
import models.AppData;
import models.Read;
import models.ReadData;
import models.RecipientMessage;
import models.WithdrawalApproval;
import models.enums.ReadType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ReadDataServiceImpl implements ReadDataService {

  private final ReadDao readDao;
  private final ReadMessageService readMessageService;

  @Inject
  public ReadDataServiceImpl(ReadDao readDao, ReadMessageService readMessageService) {
    this.readDao = readDao;
    this.readMessageService = readMessageService;
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

  private Set<String> getUnreadIds(String userId, List<? extends RecipientMessage> recipientMessages,
                                   Set<String> readIds) {
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
          readMessageService.sendRfiWithdrawalReadMessage(userId, appData.getApplication().getId(), rfiWithdrawal.getRfiId());
        });
  }

  @Override
  public void updateMessageTabReadData(String userId, AppData appData, ReadData readData) {
    String appId = appData.getApplication().getId();
    if (readData.getUnreadDelayNotificationId() != null) {
      insertRead(readData.getUnreadDelayNotificationId(), ReadType.NOTIFICATION, userId);
      readMessageService.sendNotificationReadMessage(userId, appId, readData.getUnreadDelayNotificationId());
    }
    readData.getUnreadStopNotificationIds().forEach(notificationId -> {
      insertRead(notificationId, ReadType.NOTIFICATION, userId);
      readMessageService.sendNotificationReadMessage(userId, appId, notificationId);
    });
    if (readData.getUnreadWithdrawalApprovalId() != null) {
      WithdrawalApproval withdrawalApproval = appData.getWithdrawalApproval();
      insertRead(withdrawalApproval.getId(), ReadType.WITHDRAWAL_APPROVAL, userId);
      readMessageService.sendWithdrawalRequestAcceptReadMessage(userId, withdrawalApproval.getAppId(), withdrawalApproval.getId());
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
          readMessageService.sendOutcomeReadMessage(userId, appId, outcome.getId());
        });

    readData.getUnreadInformNotificationIds().forEach(notificationId -> {
      insertRead(notificationId, ReadType.NOTIFICATION, userId);
      readMessageService.sendNotificationReadMessage(userId, appId, notificationId);
    });
  }

  private void insertRead(String relatedId, ReadType readType, String userId) {
    Read read = new Read(readId(), relatedId, readType, userId);
    readDao.insertRead(read);
  }

}

package components.service;

import com.google.inject.Inject;
import components.dao.AmendmentDao;
import components.util.Comparators;
import components.util.FileUtil;
import components.util.LinkUtil;
import components.util.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import models.AppData;
import models.Notification;
import models.ReadData;
import models.WithdrawalRejection;
import models.enums.EventLabelType;
import models.enums.MessageType;
import models.view.FileView;
import models.view.MessageReplyView;
import models.view.MessageView;
import uk.gov.bis.lite.exporterdashboard.api.Amendment;
import uk.gov.bis.lite.exporterdashboard.api.File;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

public class MessageViewServiceImpl implements MessageViewService {

  private final UserService userService;
  private final AmendmentDao amendmentDao;

  @Inject
  public MessageViewServiceImpl(UserService userService,
                                AmendmentDao amendmentDao) {
    this.userService = userService;
    this.amendmentDao = amendmentDao;
  }

  @Override
  public List<MessageView> getMessageViews(AppData appData, ReadData readData) {
    String appId = appData.getApplication().getId();
    List<MessageView> messageViews = new ArrayList<>();
    if (appData.getStopNotification() != null) {
      MessageView stopMessageView = getStopMessageView(appData.getStopNotification(), readData);
      messageViews.add(stopMessageView);
    }
    if (appData.getDelayNotification() != null) {
      MessageView delayMessageView = getDelayMessageView(appData.getDelayNotification(), readData);
      messageViews.add(delayMessageView);
    }
    messageViews.addAll(getAmendmentMessageViews(appId));
    messageViews.addAll(getWithdrawalRequestMessageViews(appData, readData));
    messageViews.sort(Comparators.MESSAGE_VIEW_CREATED_REVERSED);
    return messageViews;
  }

  private List<MessageView> getWithdrawalRequestMessageViews(AppData appData, ReadData readData) {
    List<WithdrawalRequest> withdrawalRequests = new ArrayList<>(appData.getWithdrawalRequests());
    List<WithdrawalRejection> withdrawalRejections = new ArrayList<>(appData.getWithdrawalRejections());
    withdrawalRequests.sort(Comparators.WITHDRAWAL_REQUEST_CREATED);
    withdrawalRejections.sort(Comparators.WITHDRAWAL_REJECTION_CREATED);
    List<MessageView> withdrawalRequestMessageViews = new ArrayList<>();
    for (int i = 0; i < withdrawalRequests.size(); i++) {
      WithdrawalRejection withdrawalRejection;
      if (withdrawalRejections.size() > i) {
        withdrawalRejection = withdrawalRejections.get(i);
      } else {
        withdrawalRejection = null;
      }
      MessageView messageView = getWithdrawalRequestMessageView(withdrawalRequests.get(i), withdrawalRejection, readData);
      withdrawalRequestMessageViews.add(messageView);
    }
    return withdrawalRequestMessageViews;
  }

  private MessageView getWithdrawalRequestMessageView(WithdrawalRequest withdrawalRequest, WithdrawalRejection withdrawalRejection, ReadData readData) {
    String anchor = MessageType.WITHDRAWAL_REQUESTED.toString() + "-" + withdrawalRequest.getId();
    String sentOn = TimeUtil.formatDateAndTime(withdrawalRequest.getCreatedTimestamp());
    String sender = userService.getUsername(withdrawalRequest.getCreatedByUserId());
    List<FileView> fileViews = withdrawalRequest.getAttachments().stream()
        .map(file -> getWithdrawalRequestFileView(withdrawalRequest, file)).collect(Collectors.toList());
    MessageReplyView messageReplyView = getMessageReplyView(withdrawalRejection, readData);
    return new MessageView(EventLabelType.WITHDRAWAL_REQUESTED,
        anchor,
        "Withdrawal request",
        null,
        sentOn,
        sender,
        withdrawalRequest.getMessage(),
        withdrawalRequest.getCreatedTimestamp(),
        fileViews,
        messageReplyView,
        false);
  }

  private FileView getWithdrawalRequestFileView(WithdrawalRequest withdrawalRequest, File file) {
    String size = FileUtil.getReadableFileSize(file.getUrl());
    String link = controllers.routes.DownloadController.getWithdrawalFile(withdrawalRequest.getAppId(), file.getId()).toString();
    return new FileView(file.getId(), withdrawalRequest.getId(), file.getFilename(), link, null, size);
  }

  private MessageReplyView getMessageReplyView(WithdrawalRejection withdrawalRejection, ReadData readData) {
    if (withdrawalRejection != null) {
      boolean showNewIndicator = !readData.getUnreadWithdrawalRejectionIds().isEmpty();
      String anchor = LinkUtil.getWithdrawalRejectionMessageAnchor(withdrawalRejection);
      String sender = userService.getUsername(withdrawalRejection.getCreatedByUserId());
      String withdrawnOn = TimeUtil.formatDateAndTime(withdrawalRejection.getCreatedTimestamp());
      return new MessageReplyView(anchor, "Withdrawal rejected", sender, withdrawnOn, "Your request to withdraw your application has been rejected.", showNewIndicator);
    } else {
      return null;
    }
  }

  private List<MessageView> getAmendmentMessageViews(String appId) {
    return amendmentDao.getAmendments(appId).stream()
        .map(this::getAmendmentMessageView)
        .collect(Collectors.toList());
  }

  private MessageView getAmendmentMessageView(Amendment amendment) {
    String anchor = MessageType.AMENDMENT.toString() + "-" + amendment.getId();
    String sentOn = TimeUtil.formatDateAndTime(amendment.getCreatedTimestamp());
    String sender = userService.getUsername(amendment.getCreatedByUserId());
    List<FileView> fileViews = amendment.getAttachments().stream()
        .map(file -> getAmendmentFileView(amendment, file))
        .collect(Collectors.toList());
    return new MessageView(EventLabelType.AMENDMENT_REQUESTED,
        anchor,
        "Amendment request",
        null,
        sentOn,
        sender,
        amendment.getMessage(),
        amendment.getCreatedTimestamp(),
        fileViews,
        null,
        false);
  }

  private FileView getAmendmentFileView(Amendment amendment, File file) {
    String size = FileUtil.getReadableFileSize(file.getUrl());
    String link = controllers.routes.DownloadController.getAmendmentFile(amendment.getAppId(), file.getId()).toString();
    return new FileView(file.getId(), amendment.getId(), file.getFilename(), link, null, size);
  }

  private MessageView getStopMessageView(Notification notification, ReadData readData) {
    boolean showNewIndicator = readData.getUnreadStopNotificationId() != null;
    String anchor = LinkUtil.getStoppedMessageAnchor(notification);
    String receivedOn = TimeUtil.formatDateAndTime(notification.getCreatedTimestamp());
    String sender = userService.getUsername(notification.getCreatedByUserId());
    return new MessageView(EventLabelType.STOPPED,
        anchor,
        "Application stopped",
        receivedOn,
        null,
        sender,
        notification.getMessage(),
        notification.getCreatedTimestamp(),
        new ArrayList<>(),
        null,
        showNewIndicator);
  }

  private MessageView getDelayMessageView(Notification notification, ReadData readData) {
    boolean showNewIndicator = readData.getUnreadDelayNotificationId() != null;
    String anchor = LinkUtil.getDelayedMessageAnchor(notification);
    String receivedOn = TimeUtil.formatDateAndTime(notification.getCreatedTimestamp());
    String sender = userService.getUsername(notification.getCreatedByUserId());
    return new MessageView(EventLabelType.DELAYED,
        anchor,
        "Apology for delay",
        receivedOn,
        null,
        sender,
        notification.getMessage(),
        notification.getCreatedTimestamp(),
        new ArrayList<>(),
        null,
        showNewIndicator);
  }

}

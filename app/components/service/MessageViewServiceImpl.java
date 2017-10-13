package components.service;

import com.google.inject.Inject;
import components.util.ApplicationUtil;
import components.util.Comparators;
import components.util.FileUtil;
import components.util.LinkUtil;
import components.util.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.Amendment;
import models.AppData;
import models.File;
import models.Notification;
import models.ReadData;
import models.WithdrawalRejection;
import models.WithdrawalRequest;
import models.enums.EventLabelType;
import models.enums.MessageType;
import models.view.FileView;
import models.view.MessageReplyView;
import models.view.MessageView;

public class MessageViewServiceImpl implements MessageViewService {

  private final UserService userService;

  @Inject
  public MessageViewServiceImpl(UserService userService) {
    this.userService = userService;
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
    messageViews.addAll(getAmendmentMessageViews(appData));
    messageViews.addAll(getWithdrawalRequestMessageViews(appData, readData));
    messageViews.sort(Comparators.MESSAGE_VIEW_CREATED_REVERSED);
    return messageViews;
  }

  private List<MessageView> getWithdrawalRequestMessageViews(AppData appData, ReadData readData) {
    Map<String, WithdrawalRejection> withdrawalRejectionMap = ApplicationUtil.getWithdrawalRejectionMap(appData);
    return appData.getWithdrawalRequests().stream().map(withdrawalRequest ->
        getWithdrawalRequestMessageView(withdrawalRequest, withdrawalRejectionMap.get(withdrawalRequest.getId()), readData))
        .collect(Collectors.toList());
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
    String link = controllers.routes.DownloadController.getFile(withdrawalRequest.getAppId(), file.getId()).toString();
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

  private List<MessageView> getAmendmentMessageViews(AppData appData) {
    return appData.getAmendments().stream()
        .map(this::getAmendmentMessageView)
        .collect(Collectors.toList());
  }

  private MessageView getAmendmentMessageView(Amendment amendment) {
    String anchor = LinkUtil.getAmendmentMessageAnchor(amendment);
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
    String link = controllers.routes.DownloadController.getFile(amendment.getAppId(), file.getId()).toString();
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

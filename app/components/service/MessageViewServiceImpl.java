package components.service;

import com.google.inject.Inject;
import components.common.upload.FileUtil;
import components.util.ApplicationUtil;
import components.util.Comparators;
import components.util.LinkUtil;
import components.util.TimeUtil;
import models.AmendmentRequest;
import models.AppData;
import models.Attachment;
import models.Notification;
import models.ReadData;
import models.WithdrawalApproval;
import models.WithdrawalRejection;
import models.WithdrawalRequest;
import models.enums.EventLabelType;
import models.enums.MessageType;
import models.view.FileView;
import models.view.MessageReplyView;
import models.view.MessageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageViewServiceImpl implements MessageViewService {

  private final UserService userService;

  @Inject
  public MessageViewServiceImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public List<MessageView> getMessageViews(AppData appData, ReadData readData) {
    List<MessageView> messageViews = new ArrayList<>();
    messageViews.addAll(getStopMessageViews(appData, readData));
    if (appData.getDelayNotification() != null) {
      MessageView delayMessageView = getDelayMessageView(appData.getDelayNotification(), readData);
      messageViews.add(delayMessageView);
    }
    messageViews.addAll(getAmendmentRequestMessageViews(appData));
    messageViews.addAll(getWithdrawalRequestMessageViews(appData, readData));
    messageViews.sort(Comparators.MESSAGE_VIEW_CREATED_REVERSED);
    return messageViews;
  }

  private List<MessageView> getWithdrawalRequestMessageViews(AppData appData, ReadData readData) {
    Map<String, WithdrawalRejection> withdrawalRejectionMap = ApplicationUtil.getWithdrawalRejectionMap(appData);
    WithdrawalRequest approvedWithdrawalRequest = ApplicationUtil.getApprovedWithdrawalRequest(appData);
    return appData.getWithdrawalRequests().stream()
        .map(withdrawalRequest -> {
              WithdrawalApproval withdrawalApproval;
              if (approvedWithdrawalRequest != null && approvedWithdrawalRequest.getId().equals(withdrawalRequest.getId())) {
                withdrawalApproval = appData.getWithdrawalApproval();
              } else {
                withdrawalApproval = null;
              }
              return getWithdrawalRequestMessageView(withdrawalRequest, withdrawalApproval, withdrawalRejectionMap.get(withdrawalRequest.getId()), readData);
            }
        ).collect(Collectors.toList());
  }

  private MessageView getWithdrawalRequestMessageView(WithdrawalRequest withdrawalRequest, WithdrawalApproval withdrawalApproval, WithdrawalRejection withdrawalRejection, ReadData readData) {
    String anchor = MessageType.WITHDRAWAL_REQUESTED.toString() + "-" + withdrawalRequest.getId();
    String sentOn = TimeUtil.formatDateAndTime(withdrawalRequest.getCreatedTimestamp());
    String sender = userService.getUsername(withdrawalRequest.getCreatedByUserId());
    List<FileView> fileViews = withdrawalRequest.getAttachments().stream()
        .map(attachment -> getFileView(withdrawalRequest.getAppId(), attachment))
        .collect(Collectors.toList());
    MessageReplyView messageReplyView;
    if (withdrawalApproval != null) {
      messageReplyView = getWithdrawalApprovalMessageReplyView(withdrawalApproval, readData);
    } else if (withdrawalRejection != null) {
      messageReplyView = getWithdrawalRejectionMessageReplyView(withdrawalRejection, readData);
    } else {
      messageReplyView = null;
    }
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

  private MessageReplyView getWithdrawalApprovalMessageReplyView(WithdrawalApproval withdrawalApproval, ReadData readData) {
    boolean showNewIndicator = readData.getUnreadWithdrawalApprovalId() != null;
    String anchor = LinkUtil.getWithdrawalApprovalMessageAnchor(withdrawalApproval);
    String sender = userService.getUsername(withdrawalApproval.getCreatedByUserId());
    String acceptedOn = TimeUtil.formatDateAndTime(withdrawalApproval.getCreatedTimestamp());
    return new MessageReplyView(anchor, "Withdrawal accepted", sender, acceptedOn, "Your request to withdraw your application has been accepted.", showNewIndicator);
  }

  private MessageReplyView getWithdrawalRejectionMessageReplyView(WithdrawalRejection withdrawalRejection, ReadData readData) {
    boolean showNewIndicator = readData.getUnreadWithdrawalRejectionIds().contains(withdrawalRejection.getId());
    String anchor = LinkUtil.getWithdrawalRejectionMessageAnchor(withdrawalRejection);
    String sender = userService.getUsername(withdrawalRejection.getCreatedByUserId());
    String withdrawnOn = TimeUtil.formatDateAndTime(withdrawalRejection.getCreatedTimestamp());
    return new MessageReplyView(anchor, "Withdrawal rejected", sender, withdrawnOn, "Your request to withdraw your application has been rejected.", showNewIndicator);
  }

  private List<MessageView> getAmendmentRequestMessageViews(AppData appData) {
    return appData.getAmendmentRequests().stream()
        .map(this::getAmendmentRequestMessageView)
        .collect(Collectors.toList());
  }

  private MessageView getAmendmentRequestMessageView(AmendmentRequest amendmentRequest) {
    String anchor = LinkUtil.getAmendmentRequestMessageAnchor(amendmentRequest);
    String sentOn = TimeUtil.formatDateAndTime(amendmentRequest.getCreatedTimestamp());
    String sender = userService.getUsername(amendmentRequest.getCreatedByUserId());
    List<FileView> fileViews = amendmentRequest.getAttachments().stream()
        .map(attachment -> getFileView(amendmentRequest.getAppId(), attachment))
        .collect(Collectors.toList());
    return new MessageView(EventLabelType.AMENDMENT_REQUESTED,
        anchor,
        "Amendment request",
        null,
        sentOn,
        sender,
        amendmentRequest.getMessage(),
        amendmentRequest.getCreatedTimestamp(),
        fileViews,
        null,
        false);
  }

  private FileView getFileView(String appId, Attachment attachment) {
    String size = FileUtil.getReadableFileSize(attachment.getSize());
    String link = controllers.routes.DownloadController.getAmendmentOrWithdrawalAttachment(appId, attachment.getId()).toString();
    return new FileView(attachment.getFilename(), link, size, null, null);
  }

  private List<MessageView> getStopMessageViews(AppData appData, ReadData readData) {
    return ApplicationUtil.getAllStopNotifications(appData).stream()
        .map(notification -> getStopMessageView(notification, readData))
        .collect(Collectors.toList());
  }

  private MessageView getStopMessageView(Notification notification, ReadData readData) {
    boolean showNewIndicator = readData.getUnreadStopNotificationIds().contains(notification.getId());
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
    return new MessageView(EventLabelType.DELAYED,
        anchor,
        "Apology for delay",
        receivedOn,
        null,
        null,
        notification.getMessage(),
        notification.getCreatedTimestamp(),
        new ArrayList<>(),
        null,
        showNewIndicator);
  }

}

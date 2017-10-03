package components.service;

import com.google.inject.Inject;
import components.dao.AmendmentDao;
import components.dao.NotificationDao;
import components.dao.WithdrawalRejectionDao;
import components.dao.WithdrawalRequestDao;
import components.exceptions.UnexpectedStateException;
import components.util.ApplicationUtil;
import components.util.FileUtil;
import components.util.SortUtil;
import components.util.TimeUtil;
import models.Notification;
import models.NotificationType;
import models.WithdrawalRejection;
import models.enums.EventLabelType;
import models.enums.MessageType;
import models.view.FileView;
import models.view.MessageReplyView;
import models.view.MessageView;
import uk.gov.bis.lite.exporterdashboard.api.Amendment;
import uk.gov.bis.lite.exporterdashboard.api.File;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageViewServiceImpl implements MessageViewService {

  private final NotificationDao notificationDao;
  private final UserService userService;
  private final AmendmentDao amendmentDao;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final WithdrawalRejectionDao withdrawalRejectionDao;

  @Inject
  public MessageViewServiceImpl(NotificationDao notificationDao,
                                UserService userService,
                                AmendmentDao amendmentDao,
                                WithdrawalRequestDao withdrawalRequestDao,
                                WithdrawalRejectionDao withdrawalRejectionDao) {
    this.notificationDao = notificationDao;
    this.userService = userService;
    this.amendmentDao = amendmentDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.withdrawalRejectionDao = withdrawalRejectionDao;
  }

  @Override
  public List<MessageView> getMessageViews(String appId) {
    List<MessageView> messageViews = new ArrayList<>();
    messageViews.addAll(getNotificationMessageViews(appId));
    messageViews.addAll(getAmendmentMessageViews(appId));
    messageViews.addAll(getWithdrawalRequestMessageViews(appId));
    SortUtil.reverseSortMessageViews(messageViews);
    return messageViews;
  }

  private List<MessageView> getWithdrawalRequestMessageViews(String appId) {
    List<WithdrawalRequest> withdrawalRequests = withdrawalRequestDao.getWithdrawalRequestsByAppId(appId);
    List<WithdrawalRejection> withdrawalRejections = withdrawalRejectionDao.getWithdrawalRejectionsByAppId(appId);
    SortUtil.sortWithdrawalRequests(withdrawalRequests);
    SortUtil.sortWithdrawalRejections(withdrawalRejections);
    List<MessageView> withdrawalRequestMessageViews = new ArrayList<>();
    for (int i = 0; i < withdrawalRequests.size(); i++) {
      WithdrawalRejection withdrawalRejection;
      if (withdrawalRejections.size() > i) {
        withdrawalRejection = withdrawalRejections.get(i);
      } else {
        withdrawalRejection = null;
      }
      withdrawalRequestMessageViews.add(getWithdrawalRequestMessageView(withdrawalRequests.get(i), withdrawalRejection));
    }
    return withdrawalRequestMessageViews;
  }

  private MessageView getWithdrawalRequestMessageView(WithdrawalRequest withdrawalRequest, WithdrawalRejection withdrawalRejection) {
    String anchor = MessageType.WITHDRAWAL_REQUESTED.toString() + "-" + withdrawalRequest.getId();
    String sentOn = TimeUtil.formatDateAndTime(withdrawalRequest.getCreatedTimestamp());
    String sender = userService.getUsername(withdrawalRequest.getCreatedByUserId());
    List<FileView> fileViews = withdrawalRequest.getAttachments().stream()
        .map(file -> getWithdrawalRequestFileView(withdrawalRequest, file)).collect(Collectors.toList());
    MessageReplyView messageReplyView = getMessageReplyView(withdrawalRejection);
    return new MessageView(EventLabelType.WITHDRAWAL_REQUESTED,
        anchor,
        "Withdrawal request",
        null,
        sentOn,
        sender,
        withdrawalRequest.getMessage(),
        withdrawalRequest.getCreatedTimestamp(),
        fileViews,
        messageReplyView);
  }

  private FileView getWithdrawalRequestFileView(WithdrawalRequest withdrawalRequest, File file) {
    String size = FileUtil.getReadableFileSize(file.getUrl());
    String link = controllers.routes.DownloadController.getWithdrawalFile(withdrawalRequest.getAppId(), file.getId()).toString();
    return new FileView(file.getId(), withdrawalRequest.getId(), file.getFilename(), link, null, size);
  }

  private MessageReplyView getMessageReplyView(WithdrawalRejection withdrawalRejection) {
    if (withdrawalRejection != null) {
      String anchor = ApplicationUtil.getWithdrawalRejectionMessageAnchor(withdrawalRejection);
      String sender = userService.getUsername(withdrawalRejection.getCreatedByUserId());
      String withdrawnOn = TimeUtil.formatDateAndTime(withdrawalRejection.getCreatedTimestamp());
      return new MessageReplyView(anchor, "Withdrawal rejected", sender, withdrawnOn, "Your request to withdraw your application has been rejected.");
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
        null);
  }

  private FileView getAmendmentFileView(Amendment amendment, File file) {
    String size = FileUtil.getReadableFileSize(file.getUrl());
    String link = controllers.routes.DownloadController.getAmendmentFile(amendment.getAppId(), file.getId()).toString();
    return new FileView(file.getId(), amendment.getId(), file.getFilename(), link, null, size);
  }

  private List<MessageView> getNotificationMessageViews(String appId) {
    List<Notification> notifications = notificationDao.getNotifications(appId);
    return notifications.stream()
        .filter(notification -> notification.getNotificationType() == NotificationType.STOP || notification.getNotificationType() == NotificationType.DELAY)
        .map(this::getNotificationMessageView)
        .collect(Collectors.toList());
  }

  private MessageView getNotificationMessageView(Notification notification) {
    String anchor;
    String title;
    EventLabelType eventLabelType;
    if (notification.getNotificationType() == NotificationType.STOP) {
      anchor = ApplicationUtil.getStoppedMessageAnchor(notification);
      title = "Application stopped";
      eventLabelType = EventLabelType.STOPPED;
    } else if (notification.getNotificationType() == NotificationType.DELAY) {
      anchor = ApplicationUtil.getDelayedMessageAnchor(notification);
      title = "Apology for delay";
      eventLabelType = EventLabelType.DELAYED;
    } else {
      throw new UnexpectedStateException("Unexpected notification type" + notification.getNotificationType());
    }
    String receivedOn = TimeUtil.formatDateAndTime(notification.getCreatedTimestamp());
    String sender = userService.getUsername(notification.getCreatedByUserId());
    return new MessageView(eventLabelType,
        anchor,
        title,
        receivedOn,
        null,
        sender,
        notification.getMessage(),
        notification.getCreatedTimestamp(),
        new ArrayList<>(),
        null);
  }

}

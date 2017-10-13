package components.util;

import controllers.routes;
import models.Amendment;
import models.Notification;
import models.WithdrawalRejection;
import models.WithdrawalRequest;
import models.enums.MessageType;

public class LinkUtil {

  public static String getOutcomeDocumentsLink(String appId) {
    return routes.OutcomeTabController.showOutcomeTab(appId).withFragment("outcome-documents").toString();
  }

  public static String getAmendmentMessageAnchor(Amendment amendment) {
    return MessageType.AMENDMENT.toString() + "-" + amendment.getId();
  }

  public static String getAmendmentMessageLink(Amendment amendment) {
    return routes.MessageTabController.showMessages(amendment.getAppId())
        .withFragment(getAmendmentMessageAnchor(amendment))
        .toString();

  }

  public static String getStoppedMessageAnchor(Notification notification) {
    return MessageType.STOPPED.toString() + "-" + notification.getId();
  }

  public static String getStoppedMessageLink(Notification notification) {
    return controllers.routes.MessageTabController.showMessages(notification.getAppId())
        .withFragment(getStoppedMessageAnchor(notification))
        .toString();
  }

  public static String getDelayedMessageAnchor(Notification notification) {
    return MessageType.DELAYED.toString() + "-" + notification.getId();
  }

  public static String getDelayedMessageLink(Notification notification) {
    return controllers.routes.MessageTabController.showMessages(notification.getAppId())
        .withFragment(getDelayedMessageAnchor(notification))
        .toString();
  }

  public static String getWithdrawalRequestMessageLink(WithdrawalRequest withdrawalRequest) {
    return controllers.routes.MessageTabController.showMessages(withdrawalRequest.getAppId())
        .withFragment(MessageType.WITHDRAWAL_REQUESTED + "-" + withdrawalRequest.getId())
        .toString();
  }

  public static String getWithdrawalRejectionMessageAnchor(WithdrawalRejection withdrawalRejection) {
    return MessageType.WITHDRAWAL_REJECTED + "-" + withdrawalRejection.getId();
  }

  public static String getWithdrawalRejectionMessageLink(WithdrawalRejection withdrawalRejection) {
    return controllers.routes.MessageTabController.showMessages(withdrawalRejection.getAppId())
        .withFragment(getWithdrawalRejectionMessageAnchor(withdrawalRejection))
        .toString();
  }

  public static String getInformLetterAnchor(Notification notification) {
    return "inform-letter-" + notification.getId();
  }

  public static String getInformLetterLink(Notification notification) {
    return controllers.routes.OutcomeTabController.showOutcomeTab(notification.getAppId())
        .withFragment(getInformLetterAnchor(notification))
        .toString();
  }

}

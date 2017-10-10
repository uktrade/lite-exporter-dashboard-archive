package components.util;

import models.Notification;
import models.WithdrawalRejection;
import models.enums.MessageType;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

public class LinkUtil {

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

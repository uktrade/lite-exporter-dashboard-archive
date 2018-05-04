package components.util;

import controllers.routes;
import models.AmendmentRequest;
import models.Notification;
import models.WithdrawalApproval;
import models.WithdrawalRejection;
import models.WithdrawalRequest;
import models.enums.MessageType;

public class LinkUtil {

  public static String getOutcomeDocumentsLink(String appId) {
    return routes.OutcomeTabController.showOutcomeTab(appId).withFragment("outcome-documents").toString();
  }

  public static String getAmendmentRequestMessageAnchor(AmendmentRequest amendmentRequest) {
    return MessageType.AMENDMENT_REQUESTED.toString() + "-" + amendmentRequest.getId();
  }

  public static String getAmendmentRequestMessageLink(AmendmentRequest amendmentRequest) {
    return routes.MessageTabController.showMessages(amendmentRequest.getAppId())
        .withFragment(getAmendmentRequestMessageAnchor(amendmentRequest))
        .toString();

  }

  public static String getStoppedMessageAnchor(Notification notification) {
    return MessageType.STOPPED.toString() + "-" + notification.getId();
  }

  public static String getStoppedMessageLink(String appId, Notification notification) {
    return routes.MessageTabController.showMessages(appId)
        .withFragment(getStoppedMessageAnchor(notification))
        .toString();
  }

  public static String getDelayedMessageAnchor(Notification notification) {
    return MessageType.DELAYED.toString() + "-" + notification.getId();
  }

  public static String getDelayedMessageLink(String appId, Notification notification) {
    return routes.MessageTabController.showMessages(appId)
        .withFragment(getDelayedMessageAnchor(notification))
        .toString();
  }

  public static String getWithdrawalRequestMessageLink(WithdrawalRequest withdrawalRequest) {
    return routes.MessageTabController.showMessages(withdrawalRequest.getAppId())
        .withFragment(MessageType.WITHDRAWAL_REQUESTED + "-" + withdrawalRequest.getId())
        .toString();
  }

  public static String getWithdrawalApprovalMessageAnchor(WithdrawalApproval withdrawalApproval) {
    return MessageType.WITHDRAWAL_APPROVED + "-" + withdrawalApproval.getId();
  }

  public static String getWithdrawalApprovalMessageLink(WithdrawalApproval withdrawalApproval) {
    return routes.MessageTabController.showMessages(withdrawalApproval.getAppId())
        .withFragment(getWithdrawalApprovalMessageAnchor(withdrawalApproval))
        .toString();
  }

  public static String getWithdrawalRejectionMessageAnchor(WithdrawalRejection withdrawalRejection) {
    return MessageType.WITHDRAWAL_REJECTED + "-" + withdrawalRejection.getId();
  }

  public static String getWithdrawalRejectionMessageLink(WithdrawalRejection withdrawalRejection) {
    return routes.MessageTabController.showMessages(withdrawalRejection.getAppId())
        .withFragment(getWithdrawalRejectionMessageAnchor(withdrawalRejection))
        .toString();
  }

  public static String getInformLettersLink(String appId) {
    return routes.OutcomeTabController.showOutcomeTab(appId)
        .withFragment("inform-letters")
        .toString();
  }

  public static String getRfiLink(String appId, String rfiId) {
    return controllers.routes.RfiTabController.showRfiTab(appId)
        .withFragment(rfiId)
        .toString();
  }

}

package components.util;

import java.util.UUID;

public class RandomIdUtil {

  public static String readId() {
    return random("REA");
  }

  public static String withdrawalApprovalId() {
    return random("WAP");
  }

  public static String stopNotificationId() {
    return random("STO");
  }

  public static String delayNotificationId() {
    return random("DLA");
  }

  public static String withdrawalRejectionId() {
    return random("REJ");
  }

  public static String sielId() {
    return random("SIE");
  }

  public static String fileId() {
    return random("FIL");
  }

  public static String outcomeId() {
    return random("OUT");
  }

  public static String informNotificationId() {
    return random("INF");
  }

  public static String rfiWithdrawalId() {
    return random("RWI");
  }

  public static String statusUpdateId() {
    return random("STA");
  }

  public static String appId() {
    return random("APP");
  }

  public static String rfiId() {
    return random("RFI");
  }

  public static String draftId() {
    return random("DRA");
  }

  public static String amendmentId() {
    return random("AME");
  }

  public static String rfiReplyId() {
    return random("REP");
  }

  public static String withdrawalRequestId() {
    return random("WIT");
  }

  private static String random(String prefix) {
    return prefix.toLowerCase() + "_" + UUID.randomUUID().toString().replace("-", "");
  }

}

package components.util;

import models.enums.OgelStatus;
import models.enums.SielStatus;

import java.util.EnumMap;
import java.util.Map;

public class LicenceUtil {

  private static final Map<SielStatus, String> SIEL_STATUS_NAME_MAP;

  private static final Map<OgelStatus, String> OGEL_STATUS_NAME_MAP;

  static {
    OGEL_STATUS_NAME_MAP = new EnumMap<>(OgelStatus.class);
    OGEL_STATUS_NAME_MAP.put(OgelStatus.EXTANT, "Active");
    OGEL_STATUS_NAME_MAP.put(OgelStatus.SURRENDERED, "Surrendered");
    OGEL_STATUS_NAME_MAP.put(OgelStatus.DEREGISTERED, "De-registered");
    OGEL_STATUS_NAME_MAP.put(OgelStatus.UNKNOWN, "Unknown");
  }

  static {
    SIEL_STATUS_NAME_MAP = new EnumMap<>(SielStatus.class);
    SIEL_STATUS_NAME_MAP.put(SielStatus.ACTIVE, "Active");
    SIEL_STATUS_NAME_MAP.put(SielStatus.REVOKED, "Revoked");
    SIEL_STATUS_NAME_MAP.put(SielStatus.SURRENDERED, "Surrendered");
    SIEL_STATUS_NAME_MAP.put(SielStatus.EXPIRED, "Expired");
  }

  public static String getOgelStatusName(OgelStatus ogelStatus) {
    return OGEL_STATUS_NAME_MAP.get(ogelStatus);
  }

  public static String getSielStatusName(SielStatus sielStatus) {
    return SIEL_STATUS_NAME_MAP.get(sielStatus);
  }

}

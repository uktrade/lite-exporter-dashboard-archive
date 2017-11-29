package components.util;

import uk.gov.bis.lite.permissions.api.view.LicenceView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.EnumMap;
import java.util.Map;

public class LicenceUtil {

  private static final Map<LicenceView.Status, String> SIEL_STATUS_NAME_MAP;

  private static final Map<OgelRegistrationView.Status, String> OGEL_STATUS_NAME_MAP;

  static {
    OGEL_STATUS_NAME_MAP = new EnumMap<>(OgelRegistrationView.Status.class);
    OGEL_STATUS_NAME_MAP.put(OgelRegistrationView.Status.EXTANT, "Active");
    OGEL_STATUS_NAME_MAP.put(OgelRegistrationView.Status.SURRENDERED, "Surrendered");
    OGEL_STATUS_NAME_MAP.put(OgelRegistrationView.Status.DEREGISTERED, "De-registered");
    OGEL_STATUS_NAME_MAP.put(OgelRegistrationView.Status.UNKNOWN, "Unknown");
  }

  static {
    SIEL_STATUS_NAME_MAP = new EnumMap<>(LicenceView.Status.class);
    SIEL_STATUS_NAME_MAP.put(LicenceView.Status.ACTIVE, "Active");
    SIEL_STATUS_NAME_MAP.put(LicenceView.Status.EXHAUSTED, "Exhausted");
    SIEL_STATUS_NAME_MAP.put(LicenceView.Status.EXPIRED, "Expired");
    SIEL_STATUS_NAME_MAP.put(LicenceView.Status.REVOKED, "Revoked");
    SIEL_STATUS_NAME_MAP.put(LicenceView.Status.SURRENDERED, "Surrendered");
  }

  public static String getOgelStatusName(OgelRegistrationView.Status status) {
    return OGEL_STATUS_NAME_MAP.get(status);
  }

  public static String getSielStatusName(LicenceView.Status status) {
    return SIEL_STATUS_NAME_MAP.get(status);
  }

}

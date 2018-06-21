package components.cache;

import uk.gov.bis.lite.permissions.api.view.LicenceView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.List;

public interface LicenceClientCache {
  LicenceView getLicence(String userId, String reference);

  List<LicenceView> getLicences(String userId);

  OgelRegistrationView getOgelRegistration(String userId, String reference);

  List<OgelRegistrationView> getOgelRegistrations(String userId);
}

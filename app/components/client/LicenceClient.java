package components.client;

import java.util.List;
import uk.gov.bis.lite.permissions.api.view.LicenceView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

public interface LicenceClient {

  LicenceView getLicence(String userId, String reference);

  List<LicenceView> getLicences(String userId);

  OgelRegistrationView getOgelRegistration(String userId, String reference);

  List<OgelRegistrationView> getOgelRegistrations(String userId);

}

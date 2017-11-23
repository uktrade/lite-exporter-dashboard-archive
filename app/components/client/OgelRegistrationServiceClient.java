package components.client;

import java.util.List;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

public interface OgelRegistrationServiceClient {

  OgelRegistrationView getOgelRegistration(String userId, String registrationReference);

  List<OgelRegistrationView> getOgelRegistrations(String userId);

}

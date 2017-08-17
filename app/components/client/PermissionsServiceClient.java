package components.client;

import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.List;

public interface PermissionsServiceClient {
  List<OgelRegistrationView> getOgelRegistrations(String userId);
}

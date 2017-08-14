package components.service;

import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.List;

public interface OgelRegistrationService {

  List<OgelRegistrationView> getOgelRegistrations(String userId);

}

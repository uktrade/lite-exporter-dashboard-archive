package components.service;

import java.util.Optional;
import models.view.OgelDetailsView;

public interface OgelDetailsViewService {

  Optional<OgelDetailsView> getOgelDetailsView(String userId, String registrationReference);

}

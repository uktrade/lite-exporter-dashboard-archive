package components.service;

import models.view.OgelDetailsView;

public interface OgelDetailsViewService {
  OgelDetailsView getOgelDetailsView(String userId, String registrationReference);
}

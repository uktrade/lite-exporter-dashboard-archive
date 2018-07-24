package components.service;

import models.view.OgelDetailsView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.concurrent.CompletionStage;

public interface OgelDetailsViewService {

  CompletionStage<OgelDetailsView> getOgelDetailsView(OgelRegistrationView ogelRegistrationView);

}

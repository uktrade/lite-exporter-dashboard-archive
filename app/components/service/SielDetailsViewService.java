package components.service;

import models.view.SielDetailsView;
import uk.gov.bis.lite.permissions.api.view.LicenceView;

import java.util.concurrent.CompletionStage;

public interface SielDetailsViewService {

  CompletionStage<SielDetailsView> getSielDetailsView(LicenceView licenceView);

}

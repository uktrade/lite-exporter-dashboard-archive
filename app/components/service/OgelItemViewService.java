package components.service;

import models.view.OgelItemView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface OgelItemViewService {

  CompletionStage<List<OgelItemView>> getOgelItemViews(List<OgelRegistrationView> ogelRegistrationViews);

}

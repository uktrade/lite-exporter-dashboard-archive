package components.service;

import models.view.SielItemView;
import uk.gov.bis.lite.permissions.api.view.LicenceView;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface SielItemViewService {

  CompletionStage<List<SielItemView>> getSielItemViews(List<LicenceView> licenceViews);

}

package components.service;

import java.util.Optional;
import models.view.SielDetailsView;

public interface SielDetailsViewService {

  Optional<SielDetailsView> getSielDetailsView(String userId, String caseReference);

}

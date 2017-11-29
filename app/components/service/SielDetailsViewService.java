package components.service;

import models.view.SielDetailsView;

import java.util.Optional;

public interface SielDetailsViewService {

  Optional<SielDetailsView> getSielDetailsView(String userId, String reference);

}

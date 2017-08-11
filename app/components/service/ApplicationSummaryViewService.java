package components.service;

import models.Application;
import models.StatusUpdate;
import models.view.ApplicationSummaryView;

import java.util.Collection;
import java.util.Optional;

public interface ApplicationSummaryViewService {
  ApplicationSummaryView getApplicationSummaryView(String appId);

  String getDestination(Application application);

  Optional<StatusUpdate> getMaxStatusUpdate(Collection<StatusUpdate> statusUpdates);

  String getCaseDescription(Application application);
}

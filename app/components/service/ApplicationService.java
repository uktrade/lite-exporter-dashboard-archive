package components.service;

import models.Application;
import models.StatusUpdate;

import java.util.Collection;
import java.util.Optional;

public interface ApplicationService {

  Optional<StatusUpdate> getMaxStatusUpdate(Collection<StatusUpdate> statusUpdates);

  String getDestination(Application application);

  boolean isApplicationInProgress(String appId);

}

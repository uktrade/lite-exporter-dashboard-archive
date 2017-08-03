package components.service;

import models.StatusUpdate;
import models.view.StatusItemView;

import java.util.List;

public interface StatusItemViewService {

  List<StatusItemView> getStatusItemViews(List<StatusUpdate> statusUpdates);

}

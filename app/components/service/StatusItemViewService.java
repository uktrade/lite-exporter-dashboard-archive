package components.service;

import java.util.List;
import models.AppData;
import models.view.StatusItemView;

public interface StatusItemViewService {

  List<StatusItemView> getStatusItemViews(AppData appData);

}

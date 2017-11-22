package components.service;

import models.AppData;
import models.view.StatusTrackerView;

public interface StatusTrackerViewService {

  StatusTrackerView getStatusTrackerView(AppData appData);

}

package components.service;

import models.AppData;
import models.ReadData;
import models.view.ApplicationTabsView;

public interface ApplicationTabsViewService {

  ApplicationTabsView getApplicationTabsView(AppData appData, ReadData readData);

}

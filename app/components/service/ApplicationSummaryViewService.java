package components.service;

import models.AppData;
import models.view.ApplicationSummaryView;

public interface ApplicationSummaryViewService {

  ApplicationSummaryView getApplicationSummaryView(AppData appData);

}

package components.service;

import java.util.List;
import models.AppData;
import models.view.PreviousRequestItemView;

public interface PreviousRequestItemViewService {

  List<PreviousRequestItemView> getPreviousRequestItemViews(AppData appData);
}

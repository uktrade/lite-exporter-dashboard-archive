package components.service;

import java.util.List;
import models.view.ApplicationItemView;

public interface ApplicationItemViewService {

  List<ApplicationItemView> getApplicationItemViews(String userId);

}


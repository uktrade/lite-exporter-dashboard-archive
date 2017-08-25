package components.service;

import models.view.ApplicationItemView;

import java.util.List;

public interface ApplicationItemViewService {

  List<ApplicationItemView> getApplicationItemViews(String userId);

}


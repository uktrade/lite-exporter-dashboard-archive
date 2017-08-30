package components.service;

import models.view.StatusItemView;

import java.util.List;

public interface StatusItemViewService {

  List<StatusItemView> getStatusItemViews(String appId);

}

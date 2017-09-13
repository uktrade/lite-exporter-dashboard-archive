package components.service;

import models.view.SielItemView;

import java.util.List;

public interface SielItemViewService {

  List<SielItemView> getSielItemViews(String userId);

}

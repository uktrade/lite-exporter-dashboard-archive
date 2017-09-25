package components.service;

import models.view.OgelItemView;

import java.util.List;

public interface OgelItemViewService {

  boolean hasOgelItemViews(String userId);

  List<OgelItemView> getOgelItemViews(String userId);

}

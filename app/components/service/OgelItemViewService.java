package components.service;

import models.view.OgelItemView;

import java.util.List;

public interface OgelItemViewService {

  List<OgelItemView> getOgelItemViews(String userId);

}

package components.service;

import models.view.AddRfiResponseView;
import models.view.RfiView;

import java.util.List;

public interface RfiViewService {

  List<RfiView> getRfiViews(String appId);

  int getRfiViewCount(String appId);

  AddRfiResponseView getAddRfiResponseView(String rfiId);
}

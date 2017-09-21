package components.service;

import models.view.AddRfiReplyView;
import models.view.RfiView;

import java.util.List;

public interface RfiViewService {

  List<RfiView> getRfiViews(String appId);

  int getRfiViewCount(String appId);

  AddRfiReplyView getAddRfiReplyView(String appId, String rfiId);

}

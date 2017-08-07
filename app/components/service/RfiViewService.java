package components.service;

import models.view.RfiView;

import java.util.List;

public interface RfiViewService {

  List<RfiView> getRfiViewsWithReply(String appId, String rfiId);

  List<RfiView> getRfiViews(String appId);

  int getRfiViewCount(String appId);
}

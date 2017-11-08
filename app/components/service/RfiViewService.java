package components.service;

import java.util.List;
import models.AppData;
import models.view.AddRfiReplyView;
import models.view.RfiView;

public interface RfiViewService {

  List<RfiView> getRfiViews(String userId, AppData appData);

  AddRfiReplyView getAddRfiReplyView(String appId, String rfiId);

}

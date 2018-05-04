package components.service.ogelonly;

import components.service.SielItemViewService;
import models.view.SielItemView;

import java.util.ArrayList;
import java.util.List;

public class OgelOnlySielItemViewServiceImpl implements SielItemViewService {

  @Override
  public List<SielItemView> getSielItemViews(String userId) {
    return new ArrayList<>();
  }

  @Override
  public boolean hasSielItemViews(String userId) {
    return false;
  }

}

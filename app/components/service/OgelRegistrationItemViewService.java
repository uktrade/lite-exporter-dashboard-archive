package components.service;

import models.enums.SortDirection;
import models.view.OgelRegistrationItemView;

import java.util.List;

public interface OgelRegistrationItemViewService {

  List<OgelRegistrationItemView> getOgelRegistrationItemViews(String userId, SortDirection reference, SortDirection licensee, SortDirection site, SortDirection date);

}

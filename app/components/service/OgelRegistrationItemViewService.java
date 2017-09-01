package components.service;

import models.enums.LicenceSortType;
import models.enums.SortDirection;
import models.view.OgelRegistrationItemView;

import java.util.List;

public interface OgelRegistrationItemViewService {

  List<OgelRegistrationItemView> getOgelRegistrationItemViews(String userId, LicenceSortType licenceSortType, SortDirection sortDirection);

}

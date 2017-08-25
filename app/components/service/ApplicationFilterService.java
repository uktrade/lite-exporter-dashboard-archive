package components.service;

import models.enums.ApplicationListTab;
import models.enums.StatusTypeFilter;
import models.view.ApplicationItemView;

import java.util.List;

public interface ApplicationFilterService {

  List<ApplicationItemView> filterByUser(String userId, ApplicationListTab applicationListTab, List<ApplicationItemView> applicationItemViews);

  List<ApplicationItemView> filterByCompanyId(String companyId, List<ApplicationItemView> applicationItemViews);

  List<ApplicationItemView> filterByStatusType(StatusTypeFilter statusTypeFilter, List<ApplicationItemView> applicationItemViews);

}

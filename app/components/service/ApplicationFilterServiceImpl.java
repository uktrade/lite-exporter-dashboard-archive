package components.service;

import models.enums.ApplicationListTab;
import models.enums.StatusType;
import models.enums.StatusTypeFilter;
import models.view.ApplicationItemView;

import java.util.List;
import java.util.stream.Collectors;

public class ApplicationFilterServiceImpl implements ApplicationFilterService {

  @Override
  public List<ApplicationItemView> filterByUser(String userId, ApplicationListTab applicationListTab, List<ApplicationItemView> applicationItemViews) {
    if (applicationListTab == ApplicationListTab.USER) {
      return applicationItemViews.stream()
          .filter(view -> userId.equals(view.getCreatedById()))
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

  @Override
  public List<ApplicationItemView> filterByCompanyId(String companyId, List<ApplicationItemView> applicationItemViews) {
    if (companyId != null && !companyId.equals("all")) {
      return applicationItemViews.stream()
          .filter(view -> companyId.equals(view.getCompanyId()))
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

  @Override
  public List<ApplicationItemView> filterByStatusType(StatusTypeFilter statusTypeFilter, List<ApplicationItemView> applicationItemViews) {
    switch (statusTypeFilter) {
      case DRAFT:
        return applicationItemViews.stream()
            .filter(view -> view.getSubmittedTimestamp() == null)
            .collect(Collectors.toList());
      case COMPLETED:
        return applicationItemViews.stream()
            .filter(view -> view.getStatusType() == StatusType.COMPLETE)
            .collect(Collectors.toList());
      case CURRENT:
        return applicationItemViews.stream()
            .filter(view -> view.getSubmittedTimestamp() != null && view.getStatusType() != StatusType.COMPLETE)
            .collect(Collectors.toList());
      case ALL:
      default:
        return applicationItemViews;
    }
  }

}

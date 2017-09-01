package components.util;

import models.enums.ApplicationListTab;
import models.enums.ApplicationProgress;
import models.view.ApplicationItemView;

import java.util.List;
import java.util.stream.Collectors;

public class FilterUtil {

  public static List<ApplicationItemView> filterByUser(String userId, ApplicationListTab applicationListTab, List<ApplicationItemView> applicationItemViews) {
    if (applicationListTab == ApplicationListTab.USER) {
      return applicationItemViews.stream()
          .filter(view -> userId.equals(view.getCreatedById()))
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

  public static List<ApplicationItemView> filterByCompanyId(String companyId, List<ApplicationItemView> applicationItemViews) {
    if (companyId != null && !companyId.equals("all")) {
      return applicationItemViews.stream()
          .filter(view -> companyId.equals(view.getCompanyId()))
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

  public static List<ApplicationItemView> filterByApplicationProgress(ApplicationProgress applicationProgress, List<ApplicationItemView> applicationItemViews) {
    if (applicationProgress != null) {
      return applicationItemViews.stream()
          .filter(view -> view.getApplicationProgress() == applicationProgress)
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

}

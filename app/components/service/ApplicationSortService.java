package components.service;

import models.enums.SortDirection;
import models.view.ApplicationItemView;

import java.util.List;

public interface ApplicationSortService {

  void sort(List<ApplicationItemView> applicationItemViews, SortDirection dateSortDirection, SortDirection statusSortDirection, SortDirection createdBySortDirection);
  
}

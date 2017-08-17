package components.service;

import models.enums.SortDirection;
import models.view.ApplicationItemView;

import java.util.List;

public interface ApplicationItemViewService {
  List<ApplicationItemView> getApplicationItemViews(SortDirection dateSortDirection, SortDirection statusSortDirection, SortDirection createdBySortDirection);
}

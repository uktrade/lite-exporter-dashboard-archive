package components.service;

import components.comparator.ApplicationDateComparator;
import models.enums.SortDirection;
import models.view.ApplicationItemView;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ApplicationSortServiceImpl implements ApplicationSortService {

  private static final Map<SortDirection, Comparator<ApplicationItemView>> DATE_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<ApplicationItemView>> STATUS_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<ApplicationItemView>> CREATED_BY_COMPARATORS = new EnumMap<>(SortDirection.class);

  static {
    DATE_COMPARATORS.put(SortDirection.ASC, new ApplicationDateComparator());
    DATE_COMPARATORS.put(SortDirection.DESC, new ApplicationDateComparator().reversed());
    Comparator<ApplicationItemView> statusComparator = Comparator.comparing(ApplicationItemView::getApplicationStatusTimestamp);
    STATUS_COMPARATORS.put(SortDirection.ASC, statusComparator);
    STATUS_COMPARATORS.put(SortDirection.DESC, statusComparator.reversed());
    Comparator<ApplicationItemView> createdByComparator = Comparator.comparing(ApplicationItemView::getCreatedByName);
    CREATED_BY_COMPARATORS.put(SortDirection.ASC, createdByComparator);
    CREATED_BY_COMPARATORS.put(SortDirection.DESC, createdByComparator.reversed());
  }

  @Override
  public void sort(List<ApplicationItemView> applicationItemViews, SortDirection dateSortDirection, SortDirection statusSortDirection, SortDirection createdBySortDirection) {
    if (dateSortDirection != null) {
      applicationItemViews.sort(DATE_COMPARATORS.get(dateSortDirection));
    }
    if (statusSortDirection != null) {
      applicationItemViews.sort(STATUS_COMPARATORS.get(statusSortDirection));
    }
    if (createdBySortDirection != null) {
      applicationItemViews.sort(CREATED_BY_COMPARATORS.get(createdBySortDirection));
    }
  }

}

package components.util;

import components.comparator.ApplicationDateComparator;
import models.enums.ApplicationSortType;
import models.enums.LicenceSortType;
import models.enums.SortDirection;
import models.view.ApplicationItemView;
import models.view.OgelRegistrationItemView;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SortUtil {

  private static final Map<ApplicationSortType, Map<SortDirection, Comparator<ApplicationItemView>>> APPLICATION_COMPARATORS;
  private static final Map<LicenceSortType, Map<SortDirection, Comparator<OgelRegistrationItemView>>> LICENCE_COMPARATORS;

  static {
    APPLICATION_COMPARATORS = new EnumMap<>(ApplicationSortType.class);
    APPLICATION_COMPARATORS.put(ApplicationSortType.CREATED_BY, createComparators(view -> view.getCreatedByLastName() + view.getCreatedByFirstName()));
    Comparator<ApplicationItemView> dateComparator = new ApplicationDateComparator();
    APPLICATION_COMPARATORS.put(ApplicationSortType.DATE, createComparators(dateComparator));
    Comparator<ApplicationItemView> statusComparator = Comparator.comparing(ApplicationItemView::getApplicationStatusTimestamp);
    APPLICATION_COMPARATORS.put(ApplicationSortType.STATUS, createComparators(statusComparator));
  }

  static {
    LICENCE_COMPARATORS = new EnumMap<>(LicenceSortType.class);
    LICENCE_COMPARATORS.put(LicenceSortType.REFERENCE, createComparators(OgelRegistrationItemView::getRegistrationReference));
    LICENCE_COMPARATORS.put(LicenceSortType.LICENSEE, createComparators(OgelRegistrationItemView::getLicensee));
    LICENCE_COMPARATORS.put(LicenceSortType.SITE, createComparators(OgelRegistrationItemView::getSite));
    LICENCE_COMPARATORS.put(LicenceSortType.DATE, createComparators(OgelRegistrationItemView::getRegistrationDate));
  }

  private static <T> Map<SortDirection, Comparator<T>> createComparators(Function<T, String> function) {
    return createComparators(Comparator.comparing(function));
  }

  private static <T> Map<SortDirection, Comparator<T>> createComparators(Comparator<T> comparator) {
    Map<SortDirection, Comparator<T>> comparators = new EnumMap<>(SortDirection.class);
    comparators.put(SortDirection.ASC, comparator);
    comparators.put(SortDirection.DESC, comparator.reversed());
    return comparators;
  }

  public static void sort(List<ApplicationItemView> applicationItemViewList, ApplicationSortType applicationSortType, SortDirection sortDirection) {
    applicationItemViewList.sort(APPLICATION_COMPARATORS.get(applicationSortType).get(sortDirection));
  }

  public static void sort(List<OgelRegistrationItemView> ogelRegistrationItemViews, LicenceSortType licenceSortType, SortDirection sortDirection) {
    ogelRegistrationItemViews.sort(LICENCE_COMPARATORS.get(licenceSortType).get(sortDirection));
  }

}

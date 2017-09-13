package components.util;

import components.comparator.ApplicationDateComparator;
import models.enums.ApplicationSortType;
import models.enums.LicenceSortType;
import models.enums.SortDirection;
import models.view.ApplicationItemView;
import models.view.OgelItemView;
import models.view.SielItemView;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SortUtil {

  private static final Map<ApplicationSortType, Map<SortDirection, Comparator<ApplicationItemView>>> APPLICATION_COMPARATORS;
  private static final Map<LicenceSortType, Map<SortDirection, Comparator<OgelItemView>>> OGEL_COMPARATORS;
  private static final Map<LicenceSortType, Map<SortDirection, Comparator<SielItemView>>> SIEL_COMPARATORS;

  static {
    APPLICATION_COMPARATORS = new EnumMap<>(ApplicationSortType.class);
    APPLICATION_COMPARATORS.put(ApplicationSortType.CREATED_BY, createStringComparators(view -> view.getCreatedByLastName() + view.getCreatedByFirstName()));
    Comparator<ApplicationItemView> dateComparator = new ApplicationDateComparator();
    APPLICATION_COMPARATORS.put(ApplicationSortType.DATE, createComparators(dateComparator));
    Comparator<ApplicationItemView> statusComparator = Comparator.comparing(ApplicationItemView::getApplicationStatusTimestamp);
    APPLICATION_COMPARATORS.put(ApplicationSortType.STATUS, createComparators(statusComparator));
  }

  static {
    OGEL_COMPARATORS = new EnumMap<>(LicenceSortType.class);
    OGEL_COMPARATORS.put(LicenceSortType.STATUS, createStringComparators(OgelItemView::getOgelStatus));
    OGEL_COMPARATORS.put(LicenceSortType.REFERENCE, createStringComparators(OgelItemView::getRegistrationReference));
    OGEL_COMPARATORS.put(LicenceSortType.LICENSEE, createStringComparators(OgelItemView::getLicensee));
    OGEL_COMPARATORS.put(LicenceSortType.SITE, createStringComparators(OgelItemView::getSite));
    OGEL_COMPARATORS.put(LicenceSortType.REGISTRATION_DATE, createLongComparators(OgelItemView::getRegistrationTimestamp));
  }

  static {
    SIEL_COMPARATORS = new EnumMap<>(LicenceSortType.class);
    SIEL_COMPARATORS.put(LicenceSortType.STATUS, createStringComparators(SielItemView::getSielStatus));
    SIEL_COMPARATORS.put(LicenceSortType.REFERENCE, createStringComparators(SielItemView::getRegistrationReference));
    SIEL_COMPARATORS.put(LicenceSortType.LICENSEE, createStringComparators(SielItemView::getLicensee));
    SIEL_COMPARATORS.put(LicenceSortType.EXPIRY_DATE, createLongComparators(SielItemView::getExpiryTimestamp));
  }

  private static <T> Map<SortDirection, Comparator<T>> createLongComparators(Function<T, Long> function) {
    return createComparators(Comparator.comparing(function));
  }

  private static <T> Map<SortDirection, Comparator<T>> createStringComparators(Function<T, String> function) {
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

  public static void sortOgels(List<OgelItemView> ogelItemViews, LicenceSortType licenceSortType, SortDirection sortDirection) {
    ogelItemViews.sort(OGEL_COMPARATORS.get(licenceSortType).get(sortDirection));
  }

  public static void sortSiels(List<SielItemView> sielItemViews, LicenceSortType licenceSortType, SortDirection sortDirection) {
    sielItemViews.sort(SIEL_COMPARATORS.get(licenceSortType).get(sortDirection));
  }

}

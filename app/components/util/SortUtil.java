package components.util;

import components.comparator.ApplicationReferenceComparator;
import components.comparator.ApplicationStatusComparator;
import components.comparator.DestinationComparator;
import components.comparator.EventTypeComparator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import models.Document;
import models.enums.ApplicationSortType;
import models.enums.DocumentType;
import models.enums.LicenceSortType;
import models.enums.SortDirection;
import models.view.ApplicationItemView;
import models.view.OgelItemView;
import models.view.SielItemView;

public class SortUtil {

  private static final Map<ApplicationSortType, Map<SortDirection, Comparator<ApplicationItemView>>> APPLICATION_COMPARATORS;
  private static final Map<LicenceSortType, Map<SortDirection, Comparator<OgelItemView>>> OGEL_COMPARATORS;
  private static final Map<LicenceSortType, Map<SortDirection, Comparator<SielItemView>>> SIEL_COMPARATORS;

  private static final List<DocumentType> DOCUMENT_TYPES = Arrays.asList(DocumentType.ISSUE_LETTER,
      DocumentType.AMEND_LETTER,
      DocumentType.ISSUE_LICENCE,
      DocumentType.AMEND_LICENCE,
      DocumentType.ISSUE_NLR,
      DocumentType.AMEND_NLR,
      DocumentType.ISSUE_REFUSAL,
      DocumentType.AMEND_REFUSAL);

  static {
    APPLICATION_COMPARATORS = new EnumMap<>(ApplicationSortType.class);
    APPLICATION_COMPARATORS.put(ApplicationSortType.CREATED_BY, createStringComparators(view -> view.getCreatedByLastName() + view.getCreatedByFirstName()));
    APPLICATION_COMPARATORS.put(ApplicationSortType.DATE, createLongComparators(ApplicationItemView::getDateTimestamp));
    APPLICATION_COMPARATORS.put(ApplicationSortType.STATUS, createComparators(new ApplicationStatusComparator()));
    APPLICATION_COMPARATORS.put(ApplicationSortType.EVENT_TYPE, createComparators(new EventTypeComparator()));
    APPLICATION_COMPARATORS.put(ApplicationSortType.EVENT_DATE, createLongComparators(view -> view.getForYourAttentionNotificationView().getCreatedTimestamp()));
    APPLICATION_COMPARATORS.put(ApplicationSortType.REFERENCE, createComparators(new ApplicationReferenceComparator()));
    APPLICATION_COMPARATORS.put(ApplicationSortType.DESTINATION, createComparators(new DestinationComparator()));
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

  public static void sortDocuments(List<Document> documents) {
    documents.sort(Comparator.comparingInt(document -> DOCUMENT_TYPES.indexOf(document.getDocumentType())));
  }

}

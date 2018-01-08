package components.util;

import components.comparator.ApplicationReferenceComparator;
import components.comparator.ApplicationStatusComparator;
import components.comparator.DestinationComparator;
import components.comparator.EventTypeComparator;
import models.Document;
import models.enums.ApplicationSortType;
import models.enums.DocumentType;
import models.enums.LicenceSortType;
import models.enums.SortDirection;
import models.view.ApplicationItemView;
import models.view.OgelItemView;
import models.view.SielItemView;
import org.apache.commons.collections.comparators.ComparatorChain;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SortUtil {

  private static final Map<ApplicationSortType, Map<SortDirection, Comparator<ApplicationItemView>>> APPLICATION_COMPARATORS;
  private static final Map<LicenceSortType, Map<SortDirection, Comparator<OgelItemView>>> OGEL_COMPARATORS;
  private static final Map<LicenceSortType, Map<SortDirection, Comparator<SielItemView>>> SIEL_COMPARATORS;

  private static final List<DocumentType> DOCUMENT_TYPES = Arrays.asList(DocumentType.ISSUE_COVER_LETTER,
      DocumentType.AMENDMENT_COVER_LETTER,
      DocumentType.ISSUE_LICENCE_DOCUMENT,
      DocumentType.AMENDMENT_LICENCE_DOCUMENT,
      DocumentType.ISSUE_NLR_DOCUMENT,
      DocumentType.AMENDMENT_NLR_DOCUMENT,
      DocumentType.ISSUE_REFUSE_DOCUMENT,
      DocumentType.AMENDMENT_REFUSE_DOCUMENT,
      DocumentType.ISSUE_AMENDMENT_LETTER,
      DocumentType.AMENDMENT_AMENDMENT_LETTER);

  static {
    APPLICATION_COMPARATORS = new EnumMap<>(ApplicationSortType.class);
    APPLICATION_COMPARATORS.put(ApplicationSortType.CREATED_BY, createStringComparators(view -> view.getCreatedByLastName() + view.getCreatedByFirstName()));
    APPLICATION_COMPARATORS.put(ApplicationSortType.DATE, createLongComparators(ApplicationItemView::getDateTimestamp));
    APPLICATION_COMPARATORS.put(ApplicationSortType.STATUS, createComparators(new ApplicationStatusComparator()));
    APPLICATION_COMPARATORS.put(ApplicationSortType.EVENT_TYPE, createComparators(new EventTypeComparator()));
    APPLICATION_COMPARATORS.put(ApplicationSortType.EVENT_DATE, createLongComparators(ApplicationItemView::getLatestEventTimestamp));
    APPLICATION_COMPARATORS.put(ApplicationSortType.REFERENCE, createComparators(new ApplicationReferenceComparator()));
    APPLICATION_COMPARATORS.put(ApplicationSortType.DESTINATION, createComparators(new DestinationComparator()));
  }

  static {
    OGEL_COMPARATORS = new EnumMap<>(LicenceSortType.class);
    OGEL_COMPARATORS.put(LicenceSortType.STATUS, createOgelComparators(Comparator.comparing(OgelItemView::getOgelStatus, String.CASE_INSENSITIVE_ORDER)));
    OGEL_COMPARATORS.put(LicenceSortType.REFERENCE, createStringComparators(OgelItemView::getRegistrationReference));
    OGEL_COMPARATORS.put(LicenceSortType.LICENSEE, createOgelComparators((l, r) -> (l.getLicensee() + l.getSite()).compareToIgnoreCase(r.getLicensee() + r.getSite())));
    OGEL_COMPARATORS.put(LicenceSortType.REGISTRATION_DATE, createOgelComparators(Comparator.comparing(OgelItemView::getRegistrationTimestamp)));
    OGEL_COMPARATORS.put(LicenceSortType.LAST_UPDATED, createOgelComparators(Comparator.comparing(OgelItemView::getUpdatedTimestamp)));
  }

  static {
    SIEL_COMPARATORS = new EnumMap<>(LicenceSortType.class);
    SIEL_COMPARATORS.put(LicenceSortType.STATUS, createSielComparators(Comparator.comparing(SielItemView::getSielStatus, String.CASE_INSENSITIVE_ORDER)));
    SIEL_COMPARATORS.put(LicenceSortType.REFERENCE, createStringComparators(SielItemView::getRegistrationReference));
    SIEL_COMPARATORS.put(LicenceSortType.LICENSEE, createSielComparators((l, r) -> (l.getLicensee() + l.getSite()).compareToIgnoreCase(r.getLicensee() + r.getSite())));
    SIEL_COMPARATORS.put(LicenceSortType.EXPIRY_DATE, createSielComparators(Comparator.comparing(SielItemView::getExpiryTimestamp)));
  }

  @SuppressWarnings("unchecked")
  private static Map<SortDirection, Comparator<SielItemView>> createSielComparators(Comparator<SielItemView> comparator) {
    Comparator<SielItemView> registrationReferenceComparator = Comparator.comparing(SielItemView::getRegistrationReference, String.CASE_INSENSITIVE_ORDER);
    ComparatorChain ascending = new ComparatorChain(Arrays.asList(comparator, registrationReferenceComparator));
    ComparatorChain descending = new ComparatorChain(Arrays.asList(comparator.reversed(), registrationReferenceComparator));

    Map<SortDirection, Comparator<SielItemView>> comparators = new EnumMap<>(SortDirection.class);
    comparators.put(SortDirection.ASC, ascending);
    comparators.put(SortDirection.DESC, descending);
    return comparators;
  }

  @SuppressWarnings("unchecked")
  private static Map<SortDirection, Comparator<OgelItemView>> createOgelComparators(Comparator<OgelItemView> comparator) {
    Comparator<OgelItemView> registrationReferenceComparator = Comparator.comparing(OgelItemView::getRegistrationReference, String.CASE_INSENSITIVE_ORDER);
    ComparatorChain ascending = new ComparatorChain(Arrays.asList(comparator, registrationReferenceComparator));
    ComparatorChain descending = new ComparatorChain(Arrays.asList(comparator.reversed(), registrationReferenceComparator));

    Map<SortDirection, Comparator<OgelItemView>> comparators = new EnumMap<>(SortDirection.class);
    comparators.put(SortDirection.ASC, ascending);
    comparators.put(SortDirection.DESC, descending);
    return comparators;
  }

  private static <T> Map<SortDirection, Comparator<T>> createLongComparators(Function<T, Long> function) {
    return createComparators(Comparator.comparing(function));
  }

  private static <T> Map<SortDirection, Comparator<T>> createStringComparators(Function<T, String> function) {
    return createComparators(Comparator.comparing(function, String.CASE_INSENSITIVE_ORDER));
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

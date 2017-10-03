package components.util;

import models.Document;
import models.Outcome;
import models.WithdrawalRejection;
import models.enums.ApplicationSortType;
import models.enums.DocumentType;
import models.enums.LicenceSortType;
import models.enums.SortDirection;
import models.view.ApplicationItemView;
import models.view.MessageView;
import models.view.NotificationView;
import models.view.OgelItemView;
import models.view.SielItemView;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

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
  private static final Comparator<Outcome> OUTCOME_CREATED_REVERSE_COMPARATOR = Comparator.comparing(Outcome::getCreatedTimestamp).reversed();
  private static final Comparator<MessageView> MESSAGE_VIEW_CREATED_REVERSE_COMPARATOR = Comparator.comparing(MessageView::getCreatedTimestamp).reversed();
  private static final Comparator<WithdrawalRequest> WITHDRAWAL_REQUEST_COMPARATOR = Comparator.comparing(WithdrawalRequest::getCreatedTimestamp);
  private static final Comparator<WithdrawalRejection> WITHDRAWAL_REJECTION_COMPARATOR = Comparator.comparing(WithdrawalRejection::getCreatedTimestamp);
  private static final Comparator<NotificationView> NOTIFICATION_VIEW_COMPARATOR = Comparator.comparing(NotificationView::getCreatedTimestamp);
  private static final Comparator<NotificationView> NOTIFICATION_VIEW_REVERSE_COMPARATOR = Comparator.comparing(NotificationView::getCreatedTimestamp).reversed();
  private static final Comparator<NotificationView> NOTIFICATION_VIEW_LINK_TEXT_COMPARATOR = Comparator.comparing(NotificationView::getLinkText);
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

  public static void reverseSortOutcomes(List<Outcome> outcomes) {
    outcomes.sort(OUTCOME_CREATED_REVERSE_COMPARATOR);
  }

  public static void sortDocuments(List<Document> documents) {
    documents.sort(Comparator.comparingInt(document -> DOCUMENT_TYPES.indexOf(document.getDocumentType())));
  }

  public static void reverseSortMessageViews(List<MessageView> messageViews) {
    messageViews.sort(MESSAGE_VIEW_CREATED_REVERSE_COMPARATOR);
  }

  public static void sortWithdrawalRequests(List<WithdrawalRequest> withdrawalRequests) {
    withdrawalRequests.sort(WITHDRAWAL_REQUEST_COMPARATOR);
  }

  public static void sortWithdrawalRejections(List<WithdrawalRejection> withdrawalRejections) {
    withdrawalRejections.sort(WITHDRAWAL_REJECTION_COMPARATOR);
  }

  public static void sortNotificationViewsByCreatedTimestamp(List<NotificationView> notificationViews) {
    notificationViews.sort(NOTIFICATION_VIEW_COMPARATOR);
  }

  public static void sortNotificationViewsByLinkText(List<NotificationView> notificationViews) {
    notificationViews.sort(NOTIFICATION_VIEW_LINK_TEXT_COMPARATOR);
  }

  public static void reverseSortNotificationViews(List<NotificationView> notificationViews) {
    notificationViews.sort(NOTIFICATION_VIEW_REVERSE_COMPARATOR);
  }

}

package components.service;

import static components.util.TimeUtil.time;

import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.client.OgelServiceClient;
import components.client.PermissionsServiceClient;
import models.enums.SortDirection;
import models.view.OgelRegistrationItemView;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OgelRegistrationItemViewServiceImpl implements OgelRegistrationItemViewService {

  private static final Map<SortDirection, Comparator<OgelRegistrationItemView>> REFERENCE_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<OgelRegistrationItemView>> LICENSEE_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<OgelRegistrationItemView>> SITE_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<OgelRegistrationItemView>> DATE_COMPARATORS = new EnumMap<>(SortDirection.class);

  static {
    Comparator<OgelRegistrationItemView> referenceComparator = Comparator.comparing(OgelRegistrationItemView::getRegistrationReference);
    Comparator<OgelRegistrationItemView> licenseeComparator = Comparator.comparing(OgelRegistrationItemView::getLicensee);
    Comparator<OgelRegistrationItemView> siteComparator = Comparator.comparing(OgelRegistrationItemView::getSite);
    Comparator<OgelRegistrationItemView> dateComparator = Comparator.comparing(OgelRegistrationItemView::getRegistrationDate);
    REFERENCE_COMPARATORS.put(SortDirection.ASC, referenceComparator);
    REFERENCE_COMPARATORS.put(SortDirection.DESC, referenceComparator.reversed());
    LICENSEE_COMPARATORS.put(SortDirection.ASC, licenseeComparator);
    LICENSEE_COMPARATORS.put(SortDirection.DESC, licenseeComparator.reversed());
    SITE_COMPARATORS.put(SortDirection.ASC, siteComparator);
    SITE_COMPARATORS.put(SortDirection.DESC, siteComparator.reversed());
    DATE_COMPARATORS.put(SortDirection.ASC, dateComparator);
    DATE_COMPARATORS.put(SortDirection.DESC, dateComparator.reversed());
  }

  private final PermissionsServiceClient permissionsServiceClient;
  private final TimeFormatService timeFormatService;
  private final CustomerServiceClient customerServiceClient;
  private final OgelServiceClient ogelServiceClient;

  @Inject
  public OgelRegistrationItemViewServiceImpl(PermissionsServiceClient permissionsServiceClient,
                                             TimeFormatService timeFormatService,
                                             CustomerServiceClient customerServiceClient,
                                             OgelServiceClient ogelServiceClient) {
    this.permissionsServiceClient = permissionsServiceClient;
    this.timeFormatService = timeFormatService;
    this.customerServiceClient = customerServiceClient;
    this.ogelServiceClient = ogelServiceClient;
  }

  @Override
  public List<OgelRegistrationItemView> getOgelRegistrationItemViews(String userId, SortDirection reference, SortDirection licensee, SortDirection site, SortDirection date) {
    List<OgelRegistrationView> ogelRegistrationViews = permissionsServiceClient.getOgelRegistrations(userId);
    Map<String, SiteView> sites = getSites(ogelRegistrationViews);
    Map<String, CustomerView> customers = getCustomers(ogelRegistrationViews);
    Map<String, OgelFullView> ogels = getOgels(ogelRegistrationViews);

    List<OgelRegistrationItemView> ogelRegistrationItemViews = ogelRegistrationViews.stream()
        .map(view -> {
          CustomerView customerView = customers.get(view.getCustomerId());
          SiteView siteView = sites.get(view.getSiteId());
          OgelFullView ogelFullView = ogels.get(view.getOgelType());
          return getOgelRegistrationItemView(view, customerView, siteView, ogelFullView);
        })
        .collect(Collectors.toList());

    // TODO Remove recycling of data when we have more mock data available
    ogelRegistrationItemViews = recycle(ogelRegistrationItemViews.get(0));

    sort(ogelRegistrationItemViews, reference, licensee, site, date);

    return ogelRegistrationItemViews;
  }

  private Map<String, OgelFullView> getOgels(List<OgelRegistrationView> ogelRegistrationViews) {
    return ogelRegistrationViews.stream()
        .map(OgelRegistrationView::getOgelType)
        .distinct()
        .map(ogelServiceClient::getOgel)
        .collect(Collectors.toMap(OgelFullView::getId, Function.identity()));
  }

  private Map<String, SiteView> getSites(List<OgelRegistrationView> ogelRegistrationViews) {
    return ogelRegistrationViews.stream()
        .map(OgelRegistrationView::getSiteId)
        .distinct()
        .map(customerServiceClient::getSite)
        .collect(Collectors.toMap(SiteView::getSiteId, Function.identity()));
  }

  private Map<String, CustomerView> getCustomers(List<OgelRegistrationView> ogelRegistrationViews) {
    return ogelRegistrationViews.stream()
        .map(OgelRegistrationView::getCustomerId)
        .distinct()
        .map(customerServiceClient::getCustomer)
        .collect(Collectors.toMap(CustomerView::getCustomerId, Function.identity()));
  }

  private List<OgelRegistrationItemView> recycle(OgelRegistrationItemView base) {
    List<OgelRegistrationItemView> recycledViews = new ArrayList<>();
    for (int i = 1; i < 22; i++) {
      String add = i % 2 == 0 ? "_A" : "_B";
      String time = timeFormatService.formatOgelRegistrationDate(time(2017, 2, 2 + i, 16, 20 + i));
      recycledViews.add(new OgelRegistrationItemView(base.getRegistrationReference(), base.getDescription(), base.getLicensee() + add, base.getSite() + add, time));
    }
    return recycledViews;
  }

  private void sort(List<OgelRegistrationItemView> ogelRegistrationItemViews, SortDirection reference, SortDirection licensee, SortDirection site, SortDirection date) {
    if (reference != null) {
      ogelRegistrationItemViews.sort(REFERENCE_COMPARATORS.get(reference));
    }
    if (licensee != null) {
      ogelRegistrationItemViews.sort(LICENSEE_COMPARATORS.get(licensee));
    }
    if (site != null) {
      ogelRegistrationItemViews.sort(SITE_COMPARATORS.get(site));
    }
    if (date != null) {
      ogelRegistrationItemViews.sort(DATE_COMPARATORS.get(date));
    }
  }

  private OgelRegistrationItemView getOgelRegistrationItemView(OgelRegistrationView ogelRegistrationView, CustomerView customerView, SiteView siteView, OgelFullView ogelFullView) {
    long registrationDateMillis = timeFormatService.parseOgelRegistrationDate(ogelRegistrationView.getRegistrationDate());
    String registrationDate = timeFormatService.formatDate(registrationDateMillis);
    return new OgelRegistrationItemView(ogelRegistrationView.getRegistrationReference(),
        ogelFullView.getName(),
        customerView.getCompanyName(),
        siteView.getSiteName(),
        registrationDate);
  }

}

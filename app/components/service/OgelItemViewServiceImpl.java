package components.service;

import com.google.inject.Inject;
import components.cache.CustomerServiceClientCache;
import components.cache.LicenceClientCache;
import components.cache.OgelServiceClientCache;
import components.util.EnumUtil;
import components.util.LicenceUtil;
import models.view.OgelItemView;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OgelItemViewServiceImpl implements OgelItemViewService {

  private final LicenceClientCache licenceClientCache;
  private final CustomerServiceClientCache customerServiceClientCache;
  private final OgelServiceClientCache ogelServiceClientCache;
  private final TimeService timeService;

  @Inject
  public OgelItemViewServiceImpl(LicenceClientCache licenceClientCache,
                                 CustomerServiceClientCache customerServiceClientCache,
                                 OgelServiceClientCache ogelServiceClientCache, TimeService timeService) {
    this.licenceClientCache = licenceClientCache;
    this.customerServiceClientCache = customerServiceClientCache;
    this.ogelServiceClientCache = ogelServiceClientCache;
    this.timeService = timeService;
  }

  @Override
  public boolean hasOgelItemViews(String userId) {
    return !licenceClientCache.getOgelRegistrations(userId).isEmpty();
  }

  @Override
  public List<OgelItemView> getOgelItemViews(String userId) {
    List<OgelRegistrationView> ogelRegistrationViews = licenceClientCache.getOgelRegistrations(userId);
    Map<String, SiteView> sites = getSites(ogelRegistrationViews);
    Map<String, CustomerView> customers = getCustomers(ogelRegistrationViews);
    Map<String, OgelFullView> ogels = getOgels(ogelRegistrationViews);

    return ogelRegistrationViews.stream()
        .map(view -> {
          CustomerView customerView = customers.get(view.getCustomerId());
          SiteView siteView = sites.get(view.getSiteId());
          OgelFullView ogelFullView = ogels.get(view.getOgelType());
          return getOgelItemView(view, customerView, siteView, ogelFullView);
        })
        .collect(Collectors.toList());
  }

  private Map<String, OgelFullView> getOgels(List<OgelRegistrationView> ogelRegistrationViews) {
    return ogelRegistrationViews.stream()
        .map(OgelRegistrationView::getOgelType)
        .distinct()
        .map(ogelServiceClientCache::getOgel)
        .collect(Collectors.toMap(OgelFullView::getId, Function.identity()));
  }

  private Map<String, SiteView> getSites(List<OgelRegistrationView> ogelRegistrationViews) {
    return ogelRegistrationViews.stream()
        .map(OgelRegistrationView::getSiteId)
        .distinct()
        .map(customerServiceClientCache::getSite)
        .collect(Collectors.toMap(SiteView::getSiteId, Function.identity()));
  }

  private Map<String, CustomerView> getCustomers(List<OgelRegistrationView> ogelRegistrationViews) {
    return ogelRegistrationViews.stream()
        .map(OgelRegistrationView::getCustomerId)
        .distinct()
        .map(customerServiceClientCache::getCustomer)
        .collect(Collectors.toMap(CustomerView::getCustomerId, Function.identity()));
  }

  private OgelItemView getOgelItemView(OgelRegistrationView ogelRegistrationView, CustomerView customerView,
                                       SiteView siteView, OgelFullView ogelFullView) {
    long registrationTimestamp = timeService.toMillis(timeService.parseYearMonthDate(ogelRegistrationView.getRegistrationDate()));
    String registrationDate = timeService.formatDate(registrationTimestamp);
    long updatedTimestamp;
    String updatedDate;
    if (ogelFullView.getLastUpdatedDate() != null) {
      updatedTimestamp = timeService.toMillis(ogelFullView.getLastUpdatedDate());
      updatedDate = timeService.formatDate(updatedTimestamp);
    } else {
      updatedTimestamp = 0;
      updatedDate = "-";
    }
    OgelRegistrationView.Status status = EnumUtil.parse(ogelRegistrationView.getStatus().toString(), OgelRegistrationView.Status.class, OgelRegistrationView.Status.UNKNOWN);
    String ogelStatusName = LicenceUtil.getOgelStatusName(status);
    return new OgelItemView(ogelRegistrationView.getRegistrationReference(),
        ogelFullView.getName(),
        customerView.getCompanyName(),
        siteView.getSiteName(),
        registrationDate,
        registrationTimestamp,
        updatedDate,
        updatedTimestamp,
        ogelStatusName);
  }

}

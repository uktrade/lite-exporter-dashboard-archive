package components.service;

import com.google.inject.Inject;
import components.cache.CustomerServiceClientCache;
import components.cache.LicenceClientCache;
import models.view.SielItemView;
import org.apache.commons.lang3.StringUtils;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;
import uk.gov.bis.lite.permissions.api.view.LicenceView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SielItemViewServiceImpl implements SielItemViewService {

  private final UserPermissionService userPermissionService;
  private final CustomerServiceClientCache customerServiceClientCache;
  private final LicenceClientCache licenceClientCache;
  private final TimeService timeService;

  @Inject
  public SielItemViewServiceImpl(UserPermissionService userPermissionService,
                                 CustomerServiceClientCache customerServiceClientCache,
                                 LicenceClientCache licenceClientCache, TimeService timeService) {
    this.userPermissionService = userPermissionService;
    this.customerServiceClientCache = customerServiceClientCache;
    this.licenceClientCache = licenceClientCache;
    this.timeService = timeService;
  }

  @Override
  public boolean hasSielItemViews(String userId) {
    return !licenceClientCache.getLicences(userId).isEmpty();
  }

  @Override
  public List<SielItemView> getSielItemViews(String userId) {
    List<String> customerIds = userPermissionService.getCustomerIdsWithViewingPermission(userId);
    Map<String, String> customerIdToCompanyName = customerIds.stream()
        .map(customerServiceClientCache::getCustomer)
        .collect(Collectors.toMap(CustomerView::getCustomerId, CustomerView::getCompanyName));

    List<LicenceView> licenceViews = licenceClientCache.getLicences(userId);

    Map<String, String> siteIdToNameMap = getSiteIdToNameMap(licenceViews);

    return licenceClientCache.getLicences(userId).stream()
        .map(licenceView -> {
          String companyName = customerIdToCompanyName.get(licenceView.getCustomerId());
          String siteName = siteIdToNameMap.get(licenceView.getSiteId());
          return createSielItemView(licenceView, companyName, siteName);
        })
        .collect(Collectors.toList());
  }

  private SielItemView createSielItemView(LicenceView licenceView, String companyName, String siteName) {
    long expiryTimestamp = timeService.toMillis(licenceView.getExpiryDate());
    String expiryDate = timeService.formatDate(expiryTimestamp);
    String sielStatus = StringUtils.capitalize(licenceView.getStatus().toString().toLowerCase());
    return new SielItemView(licenceView.getLicenceRef(),
        licenceView.getOriginalExporterRef(),
        companyName,
        siteName,
        expiryDate,
        expiryTimestamp,
        sielStatus);
  }

  private Map<String, String> getSiteIdToNameMap(List<LicenceView> licenceViews) {
    return licenceViews.stream()
        .map(LicenceView::getSiteId)
        .distinct()
        .map(customerServiceClientCache::getSite)
        .collect(Collectors.toMap(SiteView::getSiteId, SiteView::getSiteName));
  }

}

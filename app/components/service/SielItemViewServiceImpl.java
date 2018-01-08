package components.service;

import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.client.LicenceClient;
import components.util.TimeUtil;
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
  private final CustomerServiceClient customerServiceClient;
  private final LicenceClient licenceClient;

  @Inject
  public SielItemViewServiceImpl(UserPermissionService userPermissionService,
                                 CustomerServiceClient customerServiceClient,
                                 LicenceClient licenceClient) {
    this.userPermissionService = userPermissionService;
    this.customerServiceClient = customerServiceClient;
    this.licenceClient = licenceClient;
  }

  @Override
  public boolean hasSielItemViews(String userId) {
    return !licenceClient.getLicences(userId).isEmpty();
  }

  @Override
  public List<SielItemView> getSielItemViews(String userId) {
    List<String> customerIds = userPermissionService.getCustomerIdsWithViewingPermission(userId);
    Map<String, String> customerIdToCompanyName = customerIds.stream()
        .map(customerServiceClient::getCustomer)
        .collect(Collectors.toMap(CustomerView::getCustomerId, CustomerView::getCompanyName));

    List<LicenceView> licenceViews = licenceClient.getLicences(userId);

    Map<String, String> siteIdToNameMap = getSiteIdToNameMap(licenceViews);

    return licenceClient.getLicences(userId).stream()
        .map(licenceView -> {
          String companyName = customerIdToCompanyName.get(licenceView.getCustomerId());
          String siteName = siteIdToNameMap.get(licenceView.getSiteId());
          return createSielItemView(licenceView, companyName, siteName);
        })
        .collect(Collectors.toList());
  }

  private SielItemView createSielItemView(LicenceView licenceView, String companyName, String siteName) {
    long expiryTimestamp = TimeUtil.toMillis(licenceView.getExpiryDate());
    String expiryDate = TimeUtil.formatDate(expiryTimestamp);
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
        .map(customerServiceClient::getSite)
        .collect(Collectors.toMap(SiteView::getSiteId, SiteView::getSiteName));
  }

}

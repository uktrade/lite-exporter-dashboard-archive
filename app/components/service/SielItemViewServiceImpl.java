package components.service;

import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.client.LicenceClient;
import components.util.TimeUtil;
import models.view.SielItemView;
import org.apache.commons.lang3.StringUtils;
import uk.gov.bis.lite.customer.api.view.CustomerView;
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
  public List<SielItemView> getSielItemViews(String userId) {
    List<String> customerIds = userPermissionService.getCustomerIdsWithViewingPermission(userId);
    Map<String, String> customerIdToCompanyName = customerIds.stream()
        .map(customerServiceClient::getCustomer)
        .collect(Collectors.toMap(CustomerView::getCustomerId, CustomerView::getCompanyName));

    return licenceClient.getLicences(userId).stream()
        .map(licenceView -> createSielItemView(licenceView, customerIdToCompanyName))
        .collect(Collectors.toList());
  }

  private SielItemView createSielItemView(LicenceView licenceView, Map<String, String> customerIdToCompanyName) {
    long expiryTimestamp = TimeUtil.toMillis(licenceView.getExpiryDate());
    String expiryDate = TimeUtil.formatDate(expiryTimestamp);
    String licensee = customerIdToCompanyName.get(licenceView.getCustomerId());
    String sielStatus = StringUtils.capitalize(licenceView.getStatus().toString().toLowerCase());
    return new SielItemView(licenceView.getLicenceRef(), licenceView.getOriginalExporterRef(), licensee, expiryDate, expiryTimestamp, sielStatus);
  }

  @Override
  public boolean hasSielItemViews(String userId) {
    return !licenceClient.getLicences(userId).isEmpty();
  }

}

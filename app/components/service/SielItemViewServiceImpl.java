package components.service;

import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.dao.SielDao;
import components.util.LicenceUtil;
import components.util.TimeUtil;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.Siel;
import models.view.SielItemView;
import uk.gov.bis.lite.customer.api.view.CustomerView;

public class SielItemViewServiceImpl implements SielItemViewService {

  private final UserPrivilegeService userPrivilegeService;
  private final CustomerServiceClient customerServiceClient;
  private final SielDao sielDao;

  @Inject
  public SielItemViewServiceImpl(UserPrivilegeService userPrivilegeService,
                                 CustomerServiceClient customerServiceClient,
                                 SielDao sielDao) {
    this.userPrivilegeService = userPrivilegeService;
    this.customerServiceClient = customerServiceClient;
    this.sielDao = sielDao;
  }

  @Override
  public List<SielItemView> getSielItemViews(String userId) {
    List<String> customerIds = userPrivilegeService.getCustomerIdsWithBasicPermission(userId);

    Map<String, String> customerIdToCompanyName = customerIds.stream()
        .map(customerServiceClient::getCustomer)
        .collect(Collectors.toMap(CustomerView::getCustomerId, CustomerView::getCompanyName));

    List<Siel> siels = sielDao.getSiels(customerIds);

    return siels.stream()
        .map(siel -> createSielItemView(siel, customerIdToCompanyName))
        .collect(Collectors.toList());
  }

  @Override
  public boolean hasSielItemViews(String userId) {
    List<String> customerIds = userPrivilegeService.getCustomerIdsWithBasicPermission(userId);
    return !sielDao.getSiels(customerIds).isEmpty();
  }

  private SielItemView createSielItemView(Siel siel, Map<String, String> customerIdToCompanyName) {
    String expiryDate = TimeUtil.formatDate(siel.getExpiryTimestamp());
    String licensee = customerIdToCompanyName.get(siel.getCustomerId());
    String sielStatus = LicenceUtil.getSielStatusName(siel.getSielStatus());
    return new SielItemView(siel.getCaseReference(), siel.getApplicantReference(), licensee, expiryDate, siel.getExpiryTimestamp(), sielStatus);
  }

}

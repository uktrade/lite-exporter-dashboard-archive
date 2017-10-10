package components.service;

import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.dao.SielDao;
import components.util.LicenceUtil;
import components.util.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.Siel;
import models.view.SielItemView;
import uk.gov.bis.lite.customer.api.view.CustomerView;

public class SielItemViewServiceImpl implements SielItemViewService {

  private final CustomerServiceClient customerServiceClient;
  private final SielDao sielDao;

  @Inject
  public SielItemViewServiceImpl(CustomerServiceClient customerServiceClient, SielDao sielDao) {
    this.customerServiceClient = customerServiceClient;
    this.sielDao = sielDao;
  }

  @Override
  public List<SielItemView> getSielItemViews(String userId) {
    List<CustomerView> customerViews = customerServiceClient.getCustomers(userId);

    Map<String, String> customerIdToCompanyName = customerViews.stream()
        .collect(Collectors.toMap(CustomerView::getCustomerId, CustomerView::getCompanyName));

    List<String> customerIds = new ArrayList<>(customerIdToCompanyName.keySet());

    List<Siel> siels = sielDao.getSiels(customerIds);

    return siels.stream()
        .map(siel -> createSielItemView(siel, customerIdToCompanyName))
        .collect(Collectors.toList());
  }

  @Override
  public boolean hasSielItemViews(String userId) {
    List<String> customerIds = customerServiceClient.getCustomers(userId).stream()
        .map(CustomerView::getCustomerId)
        .collect(Collectors.toList());
    return !sielDao.getSiels(customerIds).isEmpty();
  }

  private SielItemView createSielItemView(Siel siel, Map<String, String> customerIdToCompanyName) {
    String expiryDate = TimeUtil.formatDate(siel.getExpiryTimestamp());
    String licensee = customerIdToCompanyName.get(siel.getCustomerId());
    String sielStatus = LicenceUtil.getSielStatusName(siel.getSielStatus());
    return new SielItemView(siel.getCaseReference(), siel.getApplicantReference(), licensee, expiryDate, siel.getExpiryTimestamp(), sielStatus);
  }

}

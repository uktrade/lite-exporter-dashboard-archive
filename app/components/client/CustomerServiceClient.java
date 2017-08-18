package components.client;

import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;

import java.util.List;

public interface CustomerServiceClient {
  CustomerView getCustomer(String customerId);

  List<CustomerView> getCustomers(String userId);

  SiteView getSite(String siteId);
}

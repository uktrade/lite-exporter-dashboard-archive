package components.client;

import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;

public interface CustomerServiceClient {

  CustomerView getCustomer(String customerId);

  SiteView getSite(String siteId);

}

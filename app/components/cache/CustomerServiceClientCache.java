package components.cache;

import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;

public interface CustomerServiceClientCache {

  CustomerView getCustomer(String customerId);

  SiteView getSite(String siteId);

}

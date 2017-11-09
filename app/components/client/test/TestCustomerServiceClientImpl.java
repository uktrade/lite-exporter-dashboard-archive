package components.client.test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.client.CustomerServiceClientImpl;
import components.service.UserService;
import components.util.TestUtil;
import filters.common.JwtRequestFilter;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;

public class TestCustomerServiceClientImpl extends CustomerServiceClientImpl {

  private final UserService userService;

  @Inject
  public TestCustomerServiceClientImpl(HttpExecutionContext httpExecutionContext,
                                       WSClient wsClient,
                                       @Named("customerServiceAddress") String address,
                                       @Named("customerServiceTimeout") int timeout,
                                       UserService userService,
                                       JwtRequestFilter jwtRequestFilter) {
    super(httpExecutionContext, wsClient, address, timeout, jwtRequestFilter);
    this.userService = userService;
  }

  @Override
  public CustomerView getCustomer(String customerId) {
    String unwrapCustomerId = TestUtil.unwrapCustomerId(customerId);
    CustomerView customerView = super.getCustomer(unwrapCustomerId);
    String wrapCustomerId = TestUtil.wrapCustomerId(userService.getCurrentUserId(), customerView.getCustomerId());
    customerView.setCustomerId(wrapCustomerId);
    return customerView;
  }

  @Override
  public SiteView getSite(String siteId) {
    String unwrapSiteId = TestUtil.unwrapSiteId(siteId);
    SiteView siteView = super.getSite(unwrapSiteId);
    String wrapCustomerId = TestUtil.wrapCustomerId(userService.getCurrentUserId(), siteView.getCustomerId());
    siteView.setCustomerId(wrapCustomerId);
    siteView.setSiteId(siteId);
    return siteView;
  }

}

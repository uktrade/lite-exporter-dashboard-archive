package components.client.test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.client.CustomerServiceClientImpl;
import components.service.UserService;
import components.service.test.TestDataServiceImpl;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;

import java.util.List;

public class TestCustomerServiceClientImpl extends CustomerServiceClientImpl {

  private final UserService userService;

  @Inject
  public TestCustomerServiceClientImpl(HttpExecutionContext httpExecutionContext,
                                       WSClient wsClient,
                                       @Named("customerServiceAddress") String address,
                                       @Named("customerServiceTimeout") int timeout,
                                       UserService userService) {
    super(httpExecutionContext, wsClient, address, timeout);
    this.userService = userService;
  }

  @Override
  public CustomerView getCustomer(String customerId) {
    String unwrapCustomerId = TestDataServiceImpl.unwrapCustomerId(customerId);
    CustomerView customerView = super.getCustomer(unwrapCustomerId);
    String wrapCustomerId = TestDataServiceImpl.wrapCustomerId(userService.getCurrentUserId(), customerView.getCustomerId());
    customerView.setCustomerId(wrapCustomerId);
    return customerView;
  }

  @Override
  public List<CustomerView> getCustomers(String userId) {
    List<CustomerView> customerViews = super.getCustomers(TestDataServiceImpl.APPLICANT_ID);
    customerViews.forEach(customerView -> {
      String wrapCustomerId = TestDataServiceImpl.wrapCustomerId(userService.getCurrentUserId(), customerView.getCustomerId());
      customerView.setCustomerId(wrapCustomerId);
    });
    return customerViews;
  }

  @Override
  public SiteView getSite(String siteId) {
    SiteView siteView = super.getSite(siteId);
    String wrapCustomerId = TestDataServiceImpl.wrapCustomerId(userService.getCurrentUserId(), siteView.getCustomerId());
    siteView.setCustomerId(wrapCustomerId);
    return siteView;
  }

}

package components.service.test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.client.UserServiceClient;
import components.service.UserPermissionServiceImpl;
import components.util.TestUtil;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import uk.gov.bis.lite.user.api.view.CustomerView;
import uk.gov.bis.lite.user.api.view.Role;
import uk.gov.bis.lite.user.api.view.SiteView;
import uk.gov.bis.lite.user.api.view.UserPrivilegesView;

public class TestUserPermissionServiceImpl extends UserPermissionServiceImpl {
  
  @Inject
  public TestUserPermissionServiceImpl(@Named("userServiceCacheExpiryMinutes") Long cacheExpireMinutes,
                                       UserServiceClient userServiceClient) {
    super(cacheExpireMinutes, userServiceClient);
  }

  @Override
  protected UserPrivilegesView getUserPrivilegesView(String userId) {
    // fake call just to make sure client call works - currently doesn't work for admin user (user with id 1)
    if (!TestDataServiceImpl.ADMIN.equals(userId)) {
      super.getUserPrivilegesView(userId);
    }

    // fake data
    UserPrivilegesView userPrivilegesView = new UserPrivilegesView();
    List<CustomerView> customerViews = TestDataServiceImpl.COMPANY_IDS.stream()
        .map(companyId -> {
          CustomerView customerView = new CustomerView();
          customerView.setCustomerId(TestUtil.wrapCustomerId(userId, companyId));
          customerView.setRole(Role.ADMIN);
          return customerView;
        }).collect(Collectors.toList());
    userPrivilegesView.setCustomers(customerViews);

    SiteView siteView = new SiteView();
    siteView.setRole(Role.ADMIN);
    siteView.setSiteId(TestUtil.wrapSiteId(userId, TestDataServiceImpl.SITE_ID));
    userPrivilegesView.setSites(Collections.singletonList(siteView));

    return userPrivilegesView;
  }

}

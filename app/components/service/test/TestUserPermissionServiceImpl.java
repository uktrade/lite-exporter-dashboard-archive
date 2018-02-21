package components.service.test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.client.UserServiceClient;
import components.service.UserPermissionServiceImpl;
import components.util.TestUtil;
import org.apache.commons.collections4.ListUtils;
import uk.gov.bis.lite.user.api.view.CustomerView;
import uk.gov.bis.lite.user.api.view.Role;
import uk.gov.bis.lite.user.api.view.SiteView;
import uk.gov.bis.lite.user.api.view.UserPrivilegesView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TestUserPermissionServiceImpl extends UserPermissionServiceImpl {

  @Inject
  public TestUserPermissionServiceImpl(@Named("userServiceCacheExpiryMinutes") Long cacheExpireMinutes,
                                       UserServiceClient userServiceClient) {
    super(cacheExpireMinutes, userServiceClient);
  }

  @Override
  protected UserPrivilegesView getUserPrivilegesView(String userId) {
    // call currently doesn't work for admin user (user with id 1)
    UserPrivilegesView originalUserPrivilegeView;
    if (!TestDataServiceImpl.ADMIN.equals(userId)) {
      originalUserPrivilegeView = super.getUserPrivilegesView(userId);
    } else {
      originalUserPrivilegeView = new UserPrivilegesView();
      originalUserPrivilegeView.setCustomers(new ArrayList<>());
      originalUserPrivilegeView.setSites(new ArrayList<>());
    }

    // fake data
    List<CustomerView> fakeCustomerViews = TestDataServiceImpl.COMPANY_IDS.stream()
        .map(companyId -> {
          CustomerView customerView = new CustomerView();
          customerView.setCustomerId(TestUtil.wrapCustomerId(userId, companyId));
          customerView.setRole(Role.ADMIN);
          return customerView;
        }).collect(Collectors.toList());

    SiteView fakeSiteView = new SiteView();
    fakeSiteView.setRole(Role.ADMIN);
    fakeSiteView.setSiteId(TestDataServiceImpl.SITE_ID);
    List<SiteView> fakeSiteViews = Collections.singletonList(fakeSiteView);

    List<CustomerView> allCustomers = ListUtils.union(fakeCustomerViews, originalUserPrivilegeView.getCustomers());
    List<SiteView> allSites = ListUtils.union(fakeSiteViews, originalUserPrivilegeView.getSites());

    UserPrivilegesView userPrivilegesView = new UserPrivilegesView();
    userPrivilegesView.setCustomers(allCustomers);
    userPrivilegesView.setSites(allSites);

    return userPrivilegesView;
  }

}

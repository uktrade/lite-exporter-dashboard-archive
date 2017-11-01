package components.service.test;

import com.google.inject.Inject;
import components.common.auth.SpireAuthManager;
import components.service.UserPrivilegeServiceImpl;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import models.AppData;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.user.api.view.CustomerView;
import uk.gov.bis.lite.user.api.view.Role;
import uk.gov.bis.lite.user.api.view.SiteView;
import uk.gov.bis.lite.user.api.view.UserPrivilegesView;

public class TestUserPrivilegeServiceImpl extends UserPrivilegeServiceImpl {

  private static final List<String> COMPANY_IDS = Arrays.asList(TestDataServiceImpl.COMPANY_ID_ONE, TestDataServiceImpl.COMPANY_ID_TWO, TestDataServiceImpl.COMPANY_ID_THREE);

  @Inject
  public TestUserPrivilegeServiceImpl(WSClient wsClient, SpireAuthManager spireAuthManager) {
    super(wsClient, spireAuthManager);
  }

  @Override
  protected UserPrivilegesView getUserPrivilegesView(String userId) {
    // fake call just to make sure client call works
    super.getUserPrivilegesView(userId);
    // fake data

    UserPrivilegesView userPrivilegesView = new UserPrivilegesView();
    List<CustomerView> customerViews = COMPANY_IDS.stream()
        .map(companyId -> {
          CustomerView customerView = new CustomerView();
          customerView.setCustomerId(TestDataServiceImpl.wrapCustomerId(userId, companyId));
          if (userId.equals(TestDataServiceImpl.APPLICANT_ID)) {
            customerView.setRole(Role.PREPARER);
          } else {
            customerView.setRole(Role.ADMIN);
          }
          return customerView;
        }).collect(Collectors.toList());
    userPrivilegesView.setCustomers(customerViews);

    SiteView siteView = new SiteView();
    if (userId.equals(TestDataServiceImpl.APPLICANT_ID)) {
      siteView.setRole(Role.PREPARER);
    } else {
      siteView.setRole(Role.ADMIN);
    }
    siteView.setSiteId(TestDataServiceImpl.SITE_ID);
    userPrivilegesView.setSites(Collections.singletonList(siteView));

    return userPrivilegesView;
  }

  @Override
  public boolean hasCreatorOrAdminPermission(String userId, AppData appData) {
    if (userId.equals(TestDataServiceImpl.APPLICANT_ID)) {
      return false;
    } else {
      return super.hasCreatorOrAdminPermission(userId, appData);
    }
  }

}

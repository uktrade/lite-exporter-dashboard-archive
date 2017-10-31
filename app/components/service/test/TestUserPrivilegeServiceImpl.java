package components.service.test;

import com.google.inject.Inject;
import components.common.auth.SpireAuthManager;
import components.service.UserPrivilegeServiceImpl;
import java.util.Arrays;
import java.util.Optional;
import models.AppData;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.user.api.view.CustomerView;
import uk.gov.bis.lite.user.api.view.Role;
import uk.gov.bis.lite.user.api.view.UserPrivilegesView;

public class TestUserPrivilegeServiceImpl extends UserPrivilegeServiceImpl {

  @Inject
  public TestUserPrivilegeServiceImpl(WSClient wsClient, SpireAuthManager spireAuthManager) {
    super(wsClient, spireAuthManager);
  }

  @Override
  protected Optional<UserPrivilegesView> getUserPrivilegesView(String userId) {
    Optional<UserPrivilegesView> userPrivilegesView = super.getUserPrivilegesView(userId);
    if (userPrivilegesView.isPresent()) {
      for (String customerIdIterate : Arrays.asList(TestDataServiceImpl.COMPANY_ID_ONE, TestDataServiceImpl.COMPANY_ID_TWO, TestDataServiceImpl.COMPANY_ID_THREE)) {
        CustomerView customerView = new CustomerView();
        customerView.setCustomerId(TestDataServiceImpl.wrapCustomerId(userId, customerIdIterate));
        if (userId.equals(TestDataServiceImpl.APPLICANT_ID)) {
          customerView.setRole(Role.PREPARER);
        } else {
          customerView.setRole(Role.ADMIN);
        }
        userPrivilegesView.get().getCustomers().add(customerView);
      }
      if (userId.equals(TestDataServiceImpl.APPLICANT_ID)) {
        userPrivilegesView.get().getSites().stream()
            .filter(siteView -> siteView.getSiteId().equals(TestDataServiceImpl.SITE_ID))
            .findAny()
            .ifPresent(siteView -> siteView.setRole(Role.PREPARER));
      }
    }
    return userPrivilegesView;
  }

  @Override
  public boolean hasAmendmentOrWithdrawalPermission(String userId, AppData appData) {
    if (userId.equals(TestDataServiceImpl.APPLICANT_ID)) {
      return false;
    } else {
      return super.hasAmendmentOrWithdrawalPermission(userId, appData);
    }
  }

}

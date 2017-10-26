package components.service.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Inject;
import components.common.auth.SpireAuthManager;
import components.service.UserPrivilegeServiceImpl;
import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.user.api.view.CustomerView;
import uk.gov.bis.lite.user.api.view.Role;
import uk.gov.bis.lite.user.api.view.UserPrivilegesView;

public class TestUserPrivilegeServiceImpl extends UserPrivilegeServiceImpl {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestUserPrivilegeServiceImpl.class);
  private static final ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();

  @Inject
  public TestUserPrivilegeServiceImpl(WSClient wsClient, SpireAuthManager spireAuthManager) {
    super(wsClient, spireAuthManager);
  }

  @Override
  public boolean isAccessAllowed(String userId, String siteId, String customerId) {
    boolean isAccessAllowed = super.isAccessAllowed(userId, siteId, customerId);
    LOGGER.error("isAccessAllowed for userId " + userId + " siteId " + siteId + " customerId " + customerId + " returned " + isAccessAllowed);
    return isAccessAllowed;
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
      try {
        LOGGER.error("userId: " + userId);
        LOGGER.error(WRITER.writeValueAsString(userPrivilegesView.get()));
      } catch (JsonProcessingException e) {
        LOGGER.error("Unable to log userPrivilegeData", e);
      }
    }
    return userPrivilegesView;
  }

}

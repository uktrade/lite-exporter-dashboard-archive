package components.client.test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.client.OgelRegistrationsClientImpl;
import components.service.UserService;
import components.service.test.TestDataServiceImpl;
import java.util.ArrayList;
import java.util.List;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

public class TestOgelRegistrationsClientImpl extends OgelRegistrationsClientImpl {

  private final UserService userService;

  @Inject
  public TestOgelRegistrationsClientImpl(HttpExecutionContext httpExecutionContext,
                                         WSClient wsClient,
                                         @Named("permissionsServiceAddress") String address,
                                         @Named("permissionsServiceTimeout") int timeout,
                                         UserService userService) {
    super(httpExecutionContext, wsClient, address, timeout);
    this.userService = userService;
  }

  // Siel Ogel
  // Admin: N N
  // Applicant11: Y N
  // Applicant2: Y Y
  // Applicant3: N Y
  @Override
  public List<OgelRegistrationView> getOgelRegistrations(String userId) {
    if ("1".equals(userId) || TestDataServiceImpl.APPLICANT_ID.equals(userId)) {
      return new ArrayList<>();
    } else {
      List<OgelRegistrationView> ogelRegistrationViews = super.getOgelRegistrations(TestDataServiceImpl.APPLICANT_ID);
      ogelRegistrationViews.forEach(ogelRegistrationView -> {
        String wrapCustomerId = TestDataServiceImpl.wrapCustomerId(userService.getCurrentUserId(), ogelRegistrationView.getCustomerId());
        ogelRegistrationView.setCustomerId(wrapCustomerId);
      });
      return ogelRegistrationViews;
    }
  }

}

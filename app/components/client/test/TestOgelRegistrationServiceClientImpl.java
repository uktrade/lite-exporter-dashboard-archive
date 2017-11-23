package components.client.test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.client.OgelRegistrationServiceClientImpl;
import components.service.UserService;
import components.service.test.TestDataServiceImpl;
import components.util.TestUtil;
import filters.common.JwtRequestFilter;
import java.util.ArrayList;
import java.util.List;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

public class TestOgelRegistrationServiceClientImpl extends OgelRegistrationServiceClientImpl {

  private final UserService userService;

  @Inject
  public TestOgelRegistrationServiceClientImpl(HttpExecutionContext httpExecutionContext,
                                               WSClient wsClient,
                                               @Named("permissionsServiceAddress") String address,
                                               @Named("permissionsServiceTimeout") int timeout,
                                               UserService userService,
                                               JwtRequestFilter jwtRequestFilter) {
    super(httpExecutionContext, wsClient, address, timeout, jwtRequestFilter);
    this.userService = userService;
  }

  // Siel Ogel
  // Admin: N N
  // Applicant11: Y N
  // Applicant2: Y Y
  // Applicant3: N Y
  @Override
  public List<OgelRegistrationView> getOgelRegistrations(String userId) {
    if (TestDataServiceImpl.ADMIN.equals(userId) || TestDataServiceImpl.APPLICANT_ID.equals(userId)) {
      return new ArrayList<>();
    } else {
      List<OgelRegistrationView> ogelRegistrationViews = super.getOgelRegistrations(userId);
      ogelRegistrationViews.forEach(ogelRegistrationView -> {
        String wrapCustomerId = TestUtil.wrapCustomerId(userService.getCurrentUserId(), ogelRegistrationView.getCustomerId());
        ogelRegistrationView.setCustomerId(wrapCustomerId);
        String wrapSiteId = TestUtil.wrapSiteId(userService.getCurrentUserId(), ogelRegistrationView.getSiteId());
        ogelRegistrationView.setSiteId(wrapSiteId);
      });
      return ogelRegistrationViews;
    }
  }

}

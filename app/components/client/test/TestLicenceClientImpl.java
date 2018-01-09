package components.client.test;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.client.LicenceClientImpl;
import components.service.UserService;
import components.service.test.TestDataServiceImpl;
import components.util.TestUtil;
import filters.common.JwtRequestFilter;
import org.apache.commons.collections4.ListUtils;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.permissions.api.view.LicenceView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestLicenceClientImpl extends LicenceClientImpl {

  private final UserService userService;

  @Inject
  public TestLicenceClientImpl(HttpExecutionContext httpExecutionContext,
                               WSClient wsClient,
                               @Named("permissionsServiceAddress") String address,
                               @Named("permissionsServiceTimeout") int timeout,
                               UserService userService,
                               JwtRequestFilter jwtRequestFilter) {
    super(httpExecutionContext, wsClient, address, timeout, jwtRequestFilter);
    this.userService = userService;
  }

  @Override
  public LicenceView getLicence(String userId, String reference) {
    return TestDataServiceImpl.getLicenceViews(userId).stream()
        .filter(licenceView -> licenceView.getLicenceRef().equals(reference))
        .findAny()
        .orElseGet(() -> super.getLicence(userId, reference));
  }

  @Override
  public List<LicenceView> getLicences(String userId) {
    if (TestDataServiceImpl.ADMIN.equals(userId)) {
      return new ArrayList<>();
    } else {
      List<LicenceView> additionalLicenceViews = TestDataServiceImpl.getLicenceViews(userId);
      List<LicenceView> licenceViews = super.getLicences(userId).stream()
          .peek(licenceView -> licenceView.setCustomerId(TestUtil.wrapCustomerId(userId, licenceView.getCustomerId())))
          .collect(Collectors.toList());
      return ListUtils.union(licenceViews, additionalLicenceViews);
    }
  }

  // Siel Ogel
  // Admin: N N
  // Applicant11: Y N
  // Applicant2: Y Y
  // Applicant3: Y Y
  @Override
  public List<OgelRegistrationView> getOgelRegistrations(String userId) {
    if (TestDataServiceImpl.ADMIN.equals(userId) || TestDataServiceImpl.APPLICANT_ID.equals(userId)) {
      return new ArrayList<>();
    } else {
      List<OgelRegistrationView> ogelRegistrationViews = super.getOgelRegistrations(userId);
      ogelRegistrationViews.forEach(ogelRegistrationView -> {
        String wrapCustomerId = TestUtil.wrapCustomerId(userService.getCurrentUserId(), ogelRegistrationView.getCustomerId());
        ogelRegistrationView.setCustomerId(wrapCustomerId);
      });
      return ogelRegistrationViews;
    }
  }

}

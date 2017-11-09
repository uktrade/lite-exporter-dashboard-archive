package pact.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pact.consumer.JwtTestHelper.JWT_AUTHORIZATION_HEADER;
import static pact.consumer.JwtTestHelper.TestJwtRequestFilter;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import components.client.OgelRegistrationServiceClient;
import components.client.OgelRegistrationServiceClientImpl;
import components.exceptions.ServiceException;
import filters.common.JwtRequestFilter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionsServicePact {
  private final static String PROVIDER = "lite-permissions-service";
  private final static String CONSUMER = "lite-exporter-dashboard";
  private static final String USER_ID = "123456";
  private static final Map<String, String> REQUEST_HEADERS = requestHeaders();
  private static final Map<String, String> RESPONSE_HEADERS = responseHeaders();

  private OgelRegistrationServiceClient client;

  @Rule
  public PactProviderRule mockProvider = new PactProviderRule(PROVIDER, this);

  @Before
  public void setUp() throws Exception {
    WSClient ws = WS.newClient(9999);
    JwtRequestFilter jwtRequestFilter = new TestJwtRequestFilter();
    client = new OgelRegistrationServiceClientImpl(new HttpExecutionContext(Runnable::run), ws, "http://" + mockProvider.getConfig().getHostname() + ":" + mockProvider.getConfig().getPort(), 10000, jwtRequestFilter);
  }

  private static Map<String, String> requestHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.putAll(JWT_AUTHORIZATION_HEADER);
    return headers;
  }

  private static Map<String, String> responseHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    return headers;
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment existingRegistrations(PactDslWithProvider builder) {

    DslPart ogelRegistrations = PactDslJsonArray.arrayMinLike(1)
        .stringType("siteId", "SITE_1")
        .stringType("customerId", "CUSTOMER_1")
        .stringType("registrationReference", "REG_123")
        .stringType("registrationDate", "2015-01-01")
        .stringType("status", "EXTANT")
        .stringType("ogelType", "OGL1")
        .closeObject();

    return builder
        .given("OGEL registrations exist for provided user")
        .uponReceiving("request to get OGEL registrations by user ID")
          .path("/ogel-registrations/user/" + USER_ID)
          .method("GET")
          .headers(REQUEST_HEADERS)
        .willRespondWith()
          .status(200)
          .headers(RESPONSE_HEADERS)
          .body(ogelRegistrations)
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment noRegistrations(PactDslWithProvider builder) {

    return builder
        .given("no OGEL registrations exist for provided user")
        .uponReceiving("request to get OGEL registrations by user ID")
          .path("/ogel-registrations/user/" + USER_ID)
          .method("GET")
          .headers(REQUEST_HEADERS)
        .willRespondWith()
          .status(404)
          .headers(RESPONSE_HEADERS)
        .toFragment();
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "existingRegistrations")
  public void existingRegistrationsPact() throws Exception {
    List<OgelRegistrationView> registrations = client.getOgelRegistrations(USER_ID);

    assertThat(registrations.size()).isEqualTo(1);
    OgelRegistrationView registration = registrations.get(0);
    assertThat(registration.getCustomerId()).isEqualTo("CUSTOMER_1");
    assertThat(registration.getSiteId()).isEqualTo("SITE_1");
    assertThat(registration.getRegistrationReference()).isEqualTo("REG_123");
    assertThat(registration.getRegistrationDate()).isEqualTo("2015-01-01");
    assertThat(registration.getStatus()).isEqualTo(OgelRegistrationView.Status.EXTANT);
    assertThat(registration.getOgelType()).isEqualTo("OGL1");
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "noRegistrations")
  public void noRegistrationsPact() throws Exception {
    assertThatThrownBy(() -> client.getOgelRegistrations(USER_ID))
        .isExactlyInstanceOf(ServiceException.class)
        .hasMessageContaining("Unable to get ogel registrations with user id");
  }
}

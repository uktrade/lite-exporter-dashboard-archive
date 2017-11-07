package pact.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import play.libs.ws.WSRequestExecutor;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionsServicePact {
  private final static String PROVIDER = "lite-permissions-service";
  private final static String CONSUMER = "lite-exporter-dashboard";
  private static final String USER_ID = "45356";
  private static final Map<String, String> REQUEST_HEADERS = requestHeaders();
  private static final Map<String, String> RESPONSE_HEADERS = responseHeaders();
  /*
    {
      "typ": "JWT",
      "alg": "HS256"
    }
    {
      "iss": "lite-ogel-registration",
        "exp": 1825343742,
        "jti": "Jw2OnEQBVAOjQ5ZfIM9pnw",
        "iat": 1509983743,
        "nbf": 1509983623,
        "sub": "45356",
        "email": "example@example.com",
        "fullName": "Mr Test"
    }
    Secret: demo-secret-which-is-very-long-so-as-to-hit-the-byte-requirement
  */
  private static final String JWT_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsaXRlLW9nZWwtcmVnaXN0cmF0aW9uIiwiZXhwIjoxODI1MzQzNzQyLCJqdGkiOiJKdzJPbkVRQlZBT2pRNVpmSU05cG53IiwiaWF0IjoxNTA5OTgzNzQzLCJuYmYiOjE1MDk5ODM2MjMsInN1YiI6IjQ1MzU2IiwiZW1haWwiOiJleGFtcGxlQGV4YW1wbGUuY29tIiwiZnVsbE5hbWUiOiJNciBUZXN0In0.C3xLajjSOx50bpi1dArX-jOA5wFOkw73ComRk9lev30";

  private OgelRegistrationServiceClient client;

  class TestJwtRequestFilter extends JwtRequestFilter {
    public TestJwtRequestFilter() {
      super(null, null);
    }
    @Override
    public WSRequestExecutor apply(WSRequestExecutor executor) {
      return request -> {
        request.setHeader("Authorization", "Bearer " + JWT_TOKEN);
        return executor.apply(request);
      };
    }
  }

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
    headers.put("Authorization", "Bearer " + JWT_TOKEN);
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

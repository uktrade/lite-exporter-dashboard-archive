package pact.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pact.consumer.JwtTestHelper.JWT_AUTHORIZATION_HEADER;
import static pact.consumer.JwtTestHelper.TestJwtRequestFilter;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import components.client.CustomerServiceClient;
import components.client.CustomerServiceClientImpl;
import components.exceptions.ServiceException;
import filters.common.JwtRequestFilter;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import play.test.WSTestClient;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;

public class CustomerServicePact {

  private final static String PROVIDER = "lite-customer-service";
  private final static String CONSUMER = "lite-exporter-dashboard";
  private static final String CUSTOMER_ID = "CUSTOMER_289";
  private static final String SITE_ID = "SITE_887";
  private static final Map<String, String> REQUEST_HEADERS = requestHeaders();
  private static final Map<String, String> RESPONSE_HEADERS = responseHeaders();

  private CustomerServiceClient client;

  @Rule
  public PactProviderRule mockProvider = new PactProviderRule(PROVIDER, this);

  @Before
  public void setUp() throws Exception {
    WSClient ws = WSTestClient.newClient(9999);
    JwtRequestFilter jwtRequestFilter = new TestJwtRequestFilter();
    client = new CustomerServiceClientImpl(new HttpExecutionContext(Runnable::run), ws, "http://" + mockProvider.getConfig().getHostname() + ":" + mockProvider.getConfig().getPort(), 10000, jwtRequestFilter);
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
  public PactFragment existingCustomer(PactDslWithProvider builder) {
    PactDslJsonBody customer = new PactDslJsonBody()
        .stringType("customerId", CUSTOMER_ID)
        .stringType("companyName", "Acme Ltd")
        .stringType("companyNumber", "876543")
        .stringType("shortName", "ACL")
        .stringType("organisationType")
        .stringType("registrationStatus")
        .stringType("applicantType")
        .minArrayLike("websites", 0, PactDslJsonRootValue.stringType());

    return builder
        .given("provided customer ID exists")
        .uponReceiving("request for a customer by ID")
          .path("/customers/" + CUSTOMER_ID)
          .method("GET")
          .headers(REQUEST_HEADERS)
        .willRespondWith()
          .status(200)
          .headers(RESPONSE_HEADERS)
          .body(customer)
        .toFragment();
  }


  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment missingCustomer(PactDslWithProvider builder) {
    return builder
        .given("provided customer ID does not exist")
        .uponReceiving("request for a customer by ID")
          .path("/customers/" + CUSTOMER_ID)
          .headers(JWT_AUTHORIZATION_HEADER)
          .method("GET")
        .willRespondWith()
          .status(404)
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment existingSite(PactDslWithProvider builder) {
    DslPart site = new PactDslJsonBody()
        .stringType("customerId", "CUSTOMER_626")
        .stringType("siteId", "SITE_887")
        .stringType("siteName", "Main Site")
        .object("address")
        .stringType("plainText", "1 Roadish Avenue, Townston")
        .stringType("country", "UK")
        .closeObject();

    return builder
        .given("provided site ID exists")
        .uponReceiving("request for site by ID")
          .path("/sites/" + SITE_ID)
          .method("GET")
          .headers(REQUEST_HEADERS)
        .willRespondWith()
          .status(200)
          .headers(RESPONSE_HEADERS)
          .body(site)
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment missingSite(PactDslWithProvider builder) {
    return builder
        .given("provided site ID does not exist")
        .uponReceiving("request for site by ID")
          .path("/sites/" + SITE_ID)
          .method("GET")
          .headers(REQUEST_HEADERS)
        .willRespondWith()
          .status(404)
        .toFragment();
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "existingCustomer")
  public void testCustomersEndpoint() throws Exception {
    CustomerView customer = client.getCustomer(CUSTOMER_ID);
    assertThat(customer.getCompanyName()).isEqualTo("Acme Ltd");
    assertThat(customer.getCompanyNumber()).isEqualTo("876543");
    assertThat(customer.getShortName()).isEqualTo("ACL");
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "missingCustomer")
  public void testCustomersEndpoint_MissingCustomer() throws Exception {
    assertThatThrownBy(() -> client.getCustomer(CUSTOMER_ID))
        .isExactlyInstanceOf(ServiceException.class)
        .hasMessageContaining("Unable to get customer with id " + CUSTOMER_ID);
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "existingSite")
  public void testSitesEndpoint() throws Exception {
    SiteView site = client.getSite(SITE_ID);
    assertThat(site.getCustomerId()).isEqualTo("CUSTOMER_626");
    assertThat(site.getSiteId()).isEqualTo("SITE_887");
    assertThat(site.getSiteName()).isEqualTo("Main Site");
    assertThat(site.getAddress().getPlainText()).isEqualTo("1 Roadish Avenue, Townston");
    assertThat(site.getAddress().getCountry()).isEqualTo("UK");
  }

  @Test
  @PactVerification(value = "lite-customer-service", fragment = "missingSite")
  public void testSitesEndpoint_MissingSite() throws Exception {
    assertThatThrownBy(() -> client.getSite(SITE_ID))
        .isExactlyInstanceOf(ServiceException.class)
        .hasMessageContaining("Unable to get site with id " + SITE_ID);
  }
}

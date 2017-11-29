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
import com.google.common.collect.ImmutableMap;
import components.client.LicenceClient;
import components.client.LicenceClientImpl;
import components.exceptions.ServiceException;
import filters.common.JwtRequestFilter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.permissions.api.view.LicenceView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.List;
import java.util.Map;

public class PermissionsServicePact {

  private static final String PROVIDER = "lite-permissions-service";
  private static final String CONSUMER = "lite-exporter-dashboard";
  private static final String LICENCES_URL = "/licences/user/";
  private static final String REGISTRATIONS_URL = "/ogel-registrations/user/";
  private static final String USER_ID = "123456";
  private static final String LICENCE_REFERENCE = "LIC/123";
  private static final String REGISTRATION_REFERENCE = "REG/123";
  private static final Map<String, String> RESPONSE_HEADERS = ImmutableMap.of("Content-Type", "application/json");

  private LicenceClient client;

  @Rule
  public PactProviderRule mockProvider = new PactProviderRule(PROVIDER, this);

  @Before
  public void setUp() throws Exception {
    WSClient ws = WS.newClient(9999);
    JwtRequestFilter jwtRequestFilter = new TestJwtRequestFilter();
    client = new LicenceClientImpl(new HttpExecutionContext(Runnable::run), ws, "http://" + mockProvider.getConfig().getHostname() + ":" + mockProvider.getConfig().getPort(), 10000, jwtRequestFilter);
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment existingLicence(PactDslWithProvider builder) {
    DslPart licences = PactDslJsonArray.arrayMinLike(1)
        .stringType("licenceRef", LICENCE_REFERENCE)
        .stringType("originalAppId", "originalAppId")
        .stringType("originalExporterRef", "originalExporterRef")
        .stringType("customerId", "customerId")
        .stringType("siteId", "siteId")
        .stringType("type", "SIEL")
        .stringType("subType", null)
        .stringType("issueDate", "2010-04-21")
        .stringType("expiryDate", "2020-04-21")
        .stringType("status", "ACTIVE")
        .stringType("externalDocumentUrl", "externalDocumentUrl")
        .array("countryList")
        .stringType("Germany")
        .stringType("France")
        .closeArray()
        .closeObject();

    return builder
        .given("licences exist for provided user")
        .uponReceiving("request to get the licence with the given licence reference for the provided user ID")
        .path(LICENCES_URL + USER_ID)
        .query("licenceReference=" + LICENCE_REFERENCE)
        .method("GET")
        .headers(JWT_AUTHORIZATION_HEADER)
        .willRespondWith()
        .status(200)
        .headers(RESPONSE_HEADERS)
        .body(licences)
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment noLicence(PactDslWithProvider builder) {
    return builder
        .given("no licences exist for provided user")
        .uponReceiving("request to get the licence with the given licence reference for the provided user ID")
        .path(LICENCES_URL + USER_ID)
        .query("licenceReference=" + LICENCE_REFERENCE)
        .method("GET")
        .headers(JWT_AUTHORIZATION_HEADER)
        .willRespondWith()
        .status(404)
        .headers(RESPONSE_HEADERS)
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment existingLicences(PactDslWithProvider builder) {

    DslPart licences = PactDslJsonArray.arrayMinLike(1)
        .stringType("licenceRef", LICENCE_REFERENCE)
        .stringType("originalAppId", "originalAppId")
        .stringType("originalExporterRef", "originalExporterRef")
        .stringType("customerId", "customerId")
        .stringType("siteId", "siteId")
        .stringType("type", "SIEL")
        .stringType("subType", null)
        .stringType("issueDate", "2010-04-21")
        .stringType("expiryDate", "2020-04-21")
        .stringType("status", "ACTIVE")
        .stringType("externalDocumentUrl", "externalDocumentUrl")
        .array("countryList")
        .stringType("Germany")
        .stringType("France")
        .closeArray()
        .closeObject();

    return builder
        .given("licences exist for provided user")
        .uponReceiving("request to get licences by user ID")
        .path(LICENCES_URL + USER_ID)
        .method("GET")
        .headers(JWT_AUTHORIZATION_HEADER)
        .willRespondWith()
        .status(200)
        .headers(RESPONSE_HEADERS)
        .body(licences)
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment noLicences(PactDslWithProvider builder) {

    PactDslJsonArray emptyArrayBody = new PactDslJsonArray();
    return builder
        .given("no licences exist for provided user")
        .uponReceiving("request to get licences by user ID")
        .path(LICENCES_URL + USER_ID)
        .method("GET")
        .headers(JWT_AUTHORIZATION_HEADER)
        .willRespondWith()
        .status(200)
        .headers(RESPONSE_HEADERS)
        .body(emptyArrayBody)
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment userNotFoundLicences(PactDslWithProvider builder) {

    return builder
        .given("provided user does not exist")
        .uponReceiving("request to get licences by user ID")
        .path(LICENCES_URL + USER_ID)
        .method("GET")
        .headers(JWT_AUTHORIZATION_HEADER)
        .willRespondWith()
        .status(404)
        .headers(RESPONSE_HEADERS)
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment existingRegistration(PactDslWithProvider builder) {
    DslPart ogelRegistrations = PactDslJsonArray.arrayMinLike(1)
        .stringType("siteId", "SITE_1")
        .stringType("customerId", "CUSTOMER_1")
        .stringType("registrationReference", REGISTRATION_REFERENCE)
        .stringType("registrationDate", "2015-01-01")
        .stringType("status", "EXTANT")
        .stringType("ogelType", "OGL1")
        .closeObject();

    return builder
        .given("OGEL registrations exist for provided user")
        .uponReceiving("request to get the OGEL registration with the given registration reference for the provided user ID")
        .path(REGISTRATIONS_URL + USER_ID)
        .query("registrationReference=" + REGISTRATION_REFERENCE)
        .method("GET")
        .headers(JWT_AUTHORIZATION_HEADER)
        .willRespondWith()
        .status(200)
        .headers(RESPONSE_HEADERS)
        .body(ogelRegistrations)
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment noRegistration(PactDslWithProvider builder) {
    return builder
        .given("no OGEL registrations exist for provided user")
        .uponReceiving("request to get the OGEL registration with the given registration reference for the provided user ID")
        .path(REGISTRATIONS_URL + USER_ID)
        .query("registrationReference=" + REGISTRATION_REFERENCE)
        .method("GET")
        .headers(JWT_AUTHORIZATION_HEADER)
        .willRespondWith()
        .status(404)
        .headers(RESPONSE_HEADERS)
        .toFragment();
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
        .path(REGISTRATIONS_URL + USER_ID)
        .method("GET")
        .headers(JWT_AUTHORIZATION_HEADER)
        .willRespondWith()
        .status(200)
        .headers(RESPONSE_HEADERS)
        .body(ogelRegistrations)
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment noRegistrations(PactDslWithProvider builder) {

    PactDslJsonArray emptyArrayBody = new PactDslJsonArray();
    return builder
        .given("no OGEL registrations exist for provided user")
        .uponReceiving("request to get OGEL registrations by user ID")
        .path(REGISTRATIONS_URL + USER_ID)
        .method("GET")
        .headers(JWT_AUTHORIZATION_HEADER)
        .willRespondWith()
        .status(200)
        .headers(RESPONSE_HEADERS)
        .body(emptyArrayBody)
        .toFragment();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public PactFragment userNotFound(PactDslWithProvider builder) {

    return builder
        .given("provided user does not exist")
        .uponReceiving("request to get OGEL registrations by user ID")
        .path(REGISTRATIONS_URL + USER_ID)
        .method("GET")
        .headers(JWT_AUTHORIZATION_HEADER)
        .willRespondWith()
        .status(404)
        .headers(RESPONSE_HEADERS)
        .toFragment();
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "existingLicence")
  public void existingLicencePact() {
    LicenceView licenceView = client.getLicence(USER_ID, LICENCE_REFERENCE);
    assertThat(licenceView).isNotNull();
    assertThat(licenceView.getLicenceRef()).isEqualTo(LICENCE_REFERENCE);
    assertThat(licenceView.getOriginalAppId()).isEqualTo("originalAppId");
    assertThat(licenceView.getOriginalExporterRef()).isEqualTo("originalExporterRef");
    assertThat(licenceView.getCustomerId()).isEqualTo("customerId");
    assertThat(licenceView.getSiteId()).isEqualTo("siteId");
    assertThat(licenceView.getType()).isEqualTo(LicenceView.Type.SIEL);
    assertThat(licenceView.getSubType()).isNull();
    assertThat(licenceView.getIssueDate()).isEqualTo("2010-04-21");
    assertThat(licenceView.getExpiryDate()).isEqualTo("2020-04-21");
    assertThat(licenceView.getStatus()).isEqualTo(LicenceView.Status.ACTIVE);
    assertThat(licenceView.getCountryList()).containsExactly("Germany", "France");
    assertThat(licenceView.getExternalDocumentUrl()).isEqualTo("externalDocumentUrl");
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "noLicence")
  public void noLicencePact() {
    assertThatThrownBy(() -> client.getLicence(USER_ID, LICENCE_REFERENCE))
        .isInstanceOf(ServiceException.class)
        .hasMessage("Unable to get licence with user id " + USER_ID + " and reference " + LICENCE_REFERENCE);
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "existingLicences")
  public void existingLicencesPact() throws Exception {
    List<LicenceView> licences = client.getLicences(USER_ID);

    assertThat(licences).hasSize(1);
    LicenceView licenceView = licences.get(0);
    assertThat(licenceView).isNotNull();
    assertThat(licenceView.getLicenceRef()).isEqualTo(LICENCE_REFERENCE);
    assertThat(licenceView.getOriginalAppId()).isEqualTo("originalAppId");
    assertThat(licenceView.getOriginalExporterRef()).isEqualTo("originalExporterRef");
    assertThat(licenceView.getCustomerId()).isEqualTo("customerId");
    assertThat(licenceView.getSiteId()).isEqualTo("siteId");
    assertThat(licenceView.getType()).isEqualTo(LicenceView.Type.SIEL);
    assertThat(licenceView.getSubType()).isNull();
    assertThat(licenceView.getIssueDate()).isEqualTo("2010-04-21");
    assertThat(licenceView.getExpiryDate()).isEqualTo("2020-04-21");
    assertThat(licenceView.getStatus()).isEqualTo(LicenceView.Status.ACTIVE);
    assertThat(licenceView.getCountryList()).containsExactly("Germany", "France");
    assertThat(licenceView.getExternalDocumentUrl()).isEqualTo("externalDocumentUrl");
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "noLicences")
  public void noLicencesPact() throws Exception {
    List<LicenceView> licences = client.getLicences(USER_ID);
    assertThat(licences).hasSize(0);
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "userNotFoundLicences")
  public void userNotFoundLicencesPact() throws Exception {
    assertThatThrownBy(() -> client.getLicences(USER_ID))
        .isExactlyInstanceOf(ServiceException.class)
        .hasMessageContaining("Unable to get licences with user id " + USER_ID);
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "existingRegistration")
  public void existingRegistrationPact() {
    OgelRegistrationView registration = client.getOgelRegistration(USER_ID, REGISTRATION_REFERENCE);
    assertThat(registration).isNotNull();
    assertThat(registration.getCustomerId()).isEqualTo("CUSTOMER_1");
    assertThat(registration.getSiteId()).isEqualTo("SITE_1");
    assertThat(registration.getRegistrationReference()).isEqualTo(REGISTRATION_REFERENCE);
    assertThat(registration.getRegistrationDate()).isEqualTo("2015-01-01");
    assertThat(registration.getStatus()).isEqualTo(OgelRegistrationView.Status.EXTANT);
    assertThat(registration.getOgelType()).isEqualTo("OGL1");
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "noRegistration")
  public void noRegistrationPact() {
    assertThatThrownBy(() -> client.getOgelRegistration(USER_ID, REGISTRATION_REFERENCE))
        .isInstanceOf(ServiceException.class)
        .hasMessage("Unable to get ogel registration with user id " + USER_ID + " and reference " + REGISTRATION_REFERENCE);
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "existingRegistrations")
  public void existingRegistrationsPact() throws Exception {
    List<OgelRegistrationView> registrations = client.getOgelRegistrations(USER_ID);

    assertThat(registrations).hasSize(1);
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
    List<OgelRegistrationView> registrations = client.getOgelRegistrations(USER_ID);
    assertThat(registrations.size()).isEqualTo(0);
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "userNotFound")
  public void userNotFoundPact() throws Exception {
    assertThatThrownBy(() -> client.getOgelRegistrations(USER_ID))
        .isExactlyInstanceOf(ServiceException.class)
        .hasMessageContaining("Unable to get ogel registrations with user id");
  }

}

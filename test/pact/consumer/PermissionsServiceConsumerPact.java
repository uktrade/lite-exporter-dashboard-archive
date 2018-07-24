package pact.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import components.common.client.PermissionsServiceClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pact.PactConfig;
import pact.consumer.components.common.client.CommonPermissionsServiceConsumerPact;
import play.libs.ws.WSClient;
import play.test.WSTestClient;

public class PermissionsServiceConsumerPact {

  @Rule
  public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2(PactConfig.PERMISSIONS_SERVICE, this);

  private WSClient wsClient;
  private PermissionsServiceClient client;

  @Before
  public void setup() {
    wsClient = WSTestClient.newClient(mockProvider.getPort());
    client = CommonPermissionsServiceConsumerPact.buildClient(wsClient, mockProvider);
  }

  @After
  public void teardown() throws Exception {
    wsClient.close();
  }

  @Pact(provider = PactConfig.PERMISSIONS_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact existingLicence(PactDslWithProvider builder) {
    return CommonPermissionsServiceConsumerPact.existingLicence(builder);
  }

  @Pact(provider = PactConfig.PERMISSIONS_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact noLicence(PactDslWithProvider builder) {
    return CommonPermissionsServiceConsumerPact.noLicence(builder);
  }

  @Pact(provider = PactConfig.PERMISSIONS_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact existingLicences(PactDslWithProvider builder) {
    return CommonPermissionsServiceConsumerPact.existingLicences(builder);
  }

  @Pact(provider = PactConfig.PERMISSIONS_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact noLicences(PactDslWithProvider builder) {
    return CommonPermissionsServiceConsumerPact.noLicences(builder);
  }

  @Pact(provider = PactConfig.PERMISSIONS_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact userNotFoundLicences(PactDslWithProvider builder) {
    return CommonPermissionsServiceConsumerPact.userNotFoundLicences(builder);
  }

  @Pact(provider = PactConfig.PERMISSIONS_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact existingRegistration(PactDslWithProvider builder) {
    return CommonPermissionsServiceConsumerPact.existingRegistration(builder);
  }

  @Pact(provider = PactConfig.PERMISSIONS_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact noRegistration(PactDslWithProvider builder) {
    return CommonPermissionsServiceConsumerPact.noRegistration(builder);
  }

  @Pact(provider = PactConfig.PERMISSIONS_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact existingRegistrations(PactDslWithProvider builder) {
    return CommonPermissionsServiceConsumerPact.existingRegistrations(builder);
  }

  @Pact(provider = PactConfig.PERMISSIONS_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact noRegistrations(PactDslWithProvider builder) {
    return CommonPermissionsServiceConsumerPact.noRegistrations(builder);
  }

  @Pact(provider = PactConfig.PERMISSIONS_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact userNotFoundRegistrations(PactDslWithProvider builder) {
    return CommonPermissionsServiceConsumerPact.userNotFoundRegistrations(builder);
  }

  @Test
  @PactVerification(value = PactConfig.PERMISSIONS_SERVICE, fragment = "existingLicence")
  public void existingLicencePact() throws Exception {
    CommonPermissionsServiceConsumerPact.existingLicence(client);
  }

  @Test
  @PactVerification(value = PactConfig.PERMISSIONS_SERVICE, fragment = "noLicence")
  public void noLicencePact() throws Exception {
    CommonPermissionsServiceConsumerPact.noLicence(client);
  }

  @Test
  @PactVerification(value = PactConfig.PERMISSIONS_SERVICE, fragment = "existingLicences")
  public void existingLicencesPact() throws Exception {
    CommonPermissionsServiceConsumerPact.existingLicences(client);
  }

  @Test
  @PactVerification(value = PactConfig.PERMISSIONS_SERVICE, fragment = "noLicences")
  public void noLicencesPact() throws Exception {
    CommonPermissionsServiceConsumerPact.noLicences(client);
  }

  @Test
  @PactVerification(value = PactConfig.PERMISSIONS_SERVICE, fragment = "userNotFoundLicences")
  public void userNotFoundLicencesPact() throws Exception {
    CommonPermissionsServiceConsumerPact.userNotFoundLicences(client);
  }

  @Test
  @PactVerification(value = PactConfig.PERMISSIONS_SERVICE, fragment = "existingRegistration")
  public void existingRegistrationPact() throws Exception {
    CommonPermissionsServiceConsumerPact.existingRegistration(client);
  }

  @Test
  @PactVerification(value = PactConfig.PERMISSIONS_SERVICE, fragment = "noRegistration")
  public void noRegistrationPact() {
    CommonPermissionsServiceConsumerPact.noRegistration(client);
  }

  @Test
  @PactVerification(value = PactConfig.PERMISSIONS_SERVICE, fragment = "existingRegistrations")
  public void existingRegistrationsPact() throws Exception {
    CommonPermissionsServiceConsumerPact.existingRegistrations(client);
  }

  @Test
  @PactVerification(value = PactConfig.PERMISSIONS_SERVICE, fragment = "noRegistrations")
  public void noRegistrationsPact() throws Exception {
    CommonPermissionsServiceConsumerPact.noRegistrations(client);
  }

  @Test
  @PactVerification(value = PactConfig.PERMISSIONS_SERVICE, fragment = "userNotFoundRegistrations")
  public void userNotFoundRegistrationsPact() throws Exception {
    CommonPermissionsServiceConsumerPact.userNotFoundRegistrations(client);
  }

}

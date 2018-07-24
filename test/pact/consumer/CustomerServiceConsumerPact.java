package pact.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import components.common.client.CustomerServiceClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pact.PactConfig;
import pact.consumer.components.common.client.CommonCustomerServiceConsumerPact;
import play.libs.ws.WSClient;
import play.test.WSTestClient;

public class CustomerServiceConsumerPact {

  @Rule
  public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2(PactConfig.CUSTOMER_SERVICE, this);

  private WSClient wsClient;
  private CustomerServiceClient client;

  @Before
  public void setup() {
    wsClient = WSTestClient.newClient(mockProvider.getPort());
    client = CommonCustomerServiceConsumerPact.buildClient(wsClient, mockProvider);
  }

  @After
  public void teardown() throws Exception {
    wsClient.close();
  }

  @Pact(provider = PactConfig.CUSTOMER_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact existingCustomer(PactDslWithProvider builder) {
    return CommonCustomerServiceConsumerPact.existingCustomer(builder);
  }

  @Pact(provider = PactConfig.CUSTOMER_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact missingCustomer(PactDslWithProvider builder) {
    return CommonCustomerServiceConsumerPact.missingCustomer(builder);
  }

  @Pact(provider = PactConfig.CUSTOMER_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact existingSite(PactDslWithProvider builder) {
    return CommonCustomerServiceConsumerPact.existingSite(builder);
  }

  @Pact(provider = PactConfig.CUSTOMER_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact missingSite(PactDslWithProvider builder) {
    return CommonCustomerServiceConsumerPact.missingSite(builder);
  }

  @Test
  @PactVerification(value = PactConfig.CUSTOMER_SERVICE, fragment = "existingCustomer")
  public void existingCustomerPact() throws Exception {
    CommonCustomerServiceConsumerPact.existingCustomer(client);
  }

  @Test
  @PactVerification(value = PactConfig.CUSTOMER_SERVICE, fragment = "missingCustomer")
  public void missingCustomerPact() throws Exception {
    CommonCustomerServiceConsumerPact.missingCustomer(client);
  }

  @Test
  @PactVerification(value = PactConfig.CUSTOMER_SERVICE, fragment = "existingSite")
  public void existingSitePact() throws Exception {
    CommonCustomerServiceConsumerPact.existingSite(client);
  }

  @Test
  @PactVerification(value = PactConfig.CUSTOMER_SERVICE, fragment = "missingSite")
  public void missingSitePact() throws Exception {
    CommonCustomerServiceConsumerPact.missingSite(client);
  }

}

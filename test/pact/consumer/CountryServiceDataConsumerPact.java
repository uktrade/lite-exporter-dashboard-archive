package pact.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import components.common.client.CountryServiceClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pact.PactConfig;
import pact.consumer.components.common.client.CommonCountryServiceDataConsumerPact;
import play.libs.ws.WSClient;
import play.test.WSTestClient;

public class CountryServiceDataConsumerPact {

  @Rule
  public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2(PactConfig.COUNTRY_SERVICE, this);

  private WSClient wsClient;
  private CountryServiceClient client;

  @Before
  public void setup() {
    wsClient = WSTestClient.newClient(mockProvider.getPort());
    client = CommonCountryServiceDataConsumerPact.buildCountryServiceAllClient(wsClient, mockProvider);
  }

  @After
  public void teardown() throws Exception {
    wsClient.close();
  }

  @Pact(provider = PactConfig.COUNTRY_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact countryDataExists(PactDslWithProvider builder) {
    return CommonCountryServiceDataConsumerPact.countryDataExists(builder);
  }

  @Test
  @PactVerification(value = PactConfig.COUNTRY_SERVICE, fragment = "countryDataExists")
  public void countryDataExistsPact() throws Exception {
    CommonCountryServiceDataConsumerPact.countryDataExists(client);
  }

}

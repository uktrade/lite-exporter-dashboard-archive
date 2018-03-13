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
import pact.consumer.components.common.client.CountryServiceDataConsumerPact;
import play.libs.ws.WSClient;
import play.test.WSTestClient;

public class CountryServicePact {

  private static final String PROVIDER = "lite-country-service";
  private static final String CONSUMER = "lite-exporter-dashboard";

  private WSClient ws;

  @Rule
  public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2(PROVIDER, this);

  @Before
  public void setUp() {
    ws = WSTestClient.newClient(mockProvider.getPort());
  }

  @After
  public void tearDown() throws Exception {
    ws.close();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public RequestResponsePact countryDataExists(PactDslWithProvider builder) {
    return CountryServiceDataConsumerPact.countryDataExists(builder);
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "countryDataExists")
  public void countryDataExistsTest() {
    CountryServiceClient client = CountryServiceDataConsumerPact.buildCountryServiceAllClient(mockProvider, ws);
    CountryServiceDataConsumerPact.doCountryDataExistsTest(client);
  }

}

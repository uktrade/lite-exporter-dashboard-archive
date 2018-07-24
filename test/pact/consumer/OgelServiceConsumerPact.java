package pact.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import components.common.client.OgelServiceClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pact.PactConfig;
import pact.consumer.components.common.client.CommonOgelServiceConsumerPact;
import play.libs.ws.WSClient;
import play.test.WSTestClient;

public class OgelServiceConsumerPact {

  @Rule
  public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2(PactConfig.OGEL_SERVICE, this);

  private WSClient wsClient;
  private OgelServiceClient client;

  @Before
  public void setup() {
    wsClient = WSTestClient.newClient(mockProvider.getPort());
    client = CommonOgelServiceConsumerPact.buildClient(wsClient, mockProvider);
  }

  @After
  public void teardown() throws Exception {
    wsClient.close();
  }

  @Pact(provider = PactConfig.OGEL_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact ogelExists(PactDslWithProvider builder) {
    return CommonOgelServiceConsumerPact.ogelExists(builder);
  }

  @Pact(provider = PactConfig.OGEL_SERVICE, consumer = PactConfig.CONSUMER)
  public RequestResponsePact ogelDoesNotExist(PactDslWithProvider builder) {
    return CommonOgelServiceConsumerPact.ogelDoesNotExist(builder);
  }

  @Test
  @PactVerification(value = PactConfig.OGEL_SERVICE, fragment = "ogelExists")
  public void ogelExistsPact() throws Exception {
    CommonOgelServiceConsumerPact.ogelExists(client);
  }

  @Test
  @PactVerification(value = PactConfig.OGEL_SERVICE, fragment = "ogelDoesNotExist")
  public void ogelDoesNotExistTest() throws Exception {
    CommonOgelServiceConsumerPact.ogelDoesNotExist(client);
  }

}


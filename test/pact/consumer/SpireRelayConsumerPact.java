package pact.consumer;

import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.MessagePactProviderRule;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.model.v3.messaging.MessagePact;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import components.dao.StatusUpdateDao;
import components.message.SpireRelayConsumer;
import components.message.SpireRelayConsumerImpl;
import models.StatusUpdate;
import models.enums.StatusType;
import org.junit.Rule;
import org.junit.Test;

public class SpireRelayConsumerPact {

  private final static String PROVIDER = "lite-spire-relay-service";
  private final static String CONSUMER = "lite-exporter-dashboard";

  @Rule
  public MessagePactProviderRule mockProvider = new MessagePactProviderRule(this);

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public MessagePact createPact(MessagePactBuilder builder) {
    PactDslJsonBody body = new PactDslJsonBody();
    body.stringValue("liteApplicationId", "app_id");
    body.stringValue("statusCode", "INITIAL_CHECKS");
    body.stringValue("timestamp", "123456789");

    return builder.given("initial checks have started")
        .expectsToReceive("a message with status INITIAL_CHECKS")
        .withContent(body)
        .toPact();
  }

  @Test
  @PactVerification({PROVIDER, "initial checks have started"})
  public void test() throws Exception {
    StatusUpdateDao statusUpdateDao = mock(StatusUpdateDao.class);
    Channel channel = mock(Channel.class);
    Envelope envelope = mock(Envelope.class);
    SpireRelayConsumer spireRelayConsumer = new SpireRelayConsumerImpl(statusUpdateDao, channel);

    spireRelayConsumer.handleDelivery(null, envelope, null, mockProvider.getMessage());

    StatusUpdate expected = new StatusUpdate("app_id", StatusType.INITIAL_CHECKS, 123456789L, null);
    verify(statusUpdateDao).insertStatusUpdate(refEq(expected, "endTimestamp"));
    verify(channel).basicAck(0, false);
  }

}

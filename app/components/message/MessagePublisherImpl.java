package components.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.exporterdashboard.api.ExporterDashboardMessage;

public class MessagePublisherImpl implements MessagePublisher {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessagePublisherImpl.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final String exchangeName;
  private final String publisherQueueName;
  private final ConnectionManager connectionManager;

  @Inject
  public MessagePublisherImpl(@Named("publisherExchangeName") String exchangeName,
                              @Named("publisherQueueName") String publisherQueueName,
                              ConnectionManager connectionManager) {
    this.exchangeName = exchangeName;
    this.publisherQueueName = publisherQueueName;
    this.connectionManager = connectionManager;
  }

  @Override
  public void sendMessage(RoutingKey routingKey, ExporterDashboardMessage exporterDashboardMessage) {
    Channel channel = null;
    try {
      channel = connectionManager.createChannel();
      channel.exchangeDeclare(exchangeName, "topic", true);
      channel.queueDeclare(publisherQueueName, true, false, false, null);
      channel.queueBind(publisherQueueName, exchangeName, routingKey.toString());
      channel.basicPublish(exchangeName, routingKey.toString(), new AMQP.BasicProperties.Builder().build(), MAPPER.writeValueAsBytes(exporterDashboardMessage));
    } catch (Exception exception) {
      String message = "Unable to send message to routing key " + routingKey;
      LOGGER.error(message, exception);
    } finally {
      closeChannel(channel);
    }
  }

  private void closeChannel(Channel channel) {
    if (channel != null) {
      try {
        channel.close();
      } catch (Exception exception) {
        String message = "Unable to close channel";
        LOGGER.error(message, exception);
      }
    }
  }

}

package components.message;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rabbitmq.client.Channel;
import components.exceptions.QueueException;

public class QueueManagerImpl implements QueueManager {

  @Inject
  public QueueManagerImpl(@Named("consumerQueueName") String consumerQueueName, Channel channel, SpireRelayConsumer spireRelayConsumer) {
    init(consumerQueueName, channel, spireRelayConsumer);
  }

  private void init(String consumerQueueName, Channel channel, SpireRelayConsumer spireRelayConsumer) {
    try {
      channel.queueDeclare(consumerQueueName, true, false, false, null);
      channel.basicConsume(consumerQueueName, false, spireRelayConsumer);
    } catch (Exception exception) {
      String message = "Unable to listen to rabbitMQ queue " + consumerQueueName;
      throw new QueueException(message, exception);
    }
  }

}

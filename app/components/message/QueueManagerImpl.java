package components.message;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rabbitmq.client.Channel;
import components.exceptions.QueueException;

public class QueueManagerImpl implements QueueManager {

  @Inject
  public QueueManagerImpl(@Named("consumerExchangeName") String consumerExchangeName,
                          @Named("consumerQueueName") String consumerQueueName,
                          Channel channel,
                          MessageConsumer messageConsumer) {
    init(consumerExchangeName, consumerQueueName, channel, messageConsumer);
  }

  private void init(String consumerExchangeName, String consumerQueueName, Channel channel, MessageConsumer messageConsumer) {
    try {
      channel.exchangeDeclare(consumerExchangeName, "topic", true);
      channel.queueDeclare(consumerQueueName, true, false, false, null);
      channel.queueBind(consumerQueueName, consumerExchangeName, "#");
      channel.basicConsume(consumerQueueName, false, messageConsumer);
    } catch (Exception exception) {
      String message = "Unable to listen to rabbitMq queue " + consumerQueueName;
      throw new QueueException(message, exception);
    }
  }

}

package components.message;

import com.rabbitmq.client.Channel;

public interface ConnectionManager {

  Channel createChannel();

}

package components.message;

import models.enums.RoutingKey;

public interface MessagePublisher {

  void sendMessage(RoutingKey routingKey, Object object);

}

package components.message;

import models.enums.RoutingKey;

public interface SpireRelayPublisher {

  void sendMessage(RoutingKey routingKey, Object object);

}

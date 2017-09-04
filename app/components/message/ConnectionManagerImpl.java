package components.message;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import components.exceptions.QueueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.inject.ApplicationLifecycle;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ConnectionManagerImpl implements ConnectionManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManagerImpl.class);
  private final String rabbitMqUrl;
  private volatile Connection connection;

  @Inject
  public ConnectionManagerImpl(ApplicationLifecycle lifecycle, @Named("rabbitMqUrl") String rabbitMqUrl) {
    this.rabbitMqUrl = rabbitMqUrl;
    lifecycle.addStopHook(() -> {
      connection.close();
      return CompletableFuture.completedFuture(null);
    });
  }

  private Connection getConnection() {
    if (connection != null && connection.isOpen()) {
      return connection;
    } else {
      try {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(rabbitMqUrl);
        connection = factory.newConnection();
        return connection;
      } catch (Exception exception) {
        String errorMessage = "Unable to connect to rabbitMQ at url " + rabbitMqUrl;
        LOGGER.error(errorMessage, exception);
        throw new QueueException(errorMessage, exception);
      }
    }
  }

  @Override
  public synchronized Channel createChannel() {
    try {
      return getConnection().createChannel();
    } catch (IOException ioe) {
      String errorMessage = "Unable to init rabbitMq channel at url " + rabbitMqUrl;
      LOGGER.error(errorMessage, ioe);
      throw new QueueException(errorMessage, ioe);
    }
  }

}

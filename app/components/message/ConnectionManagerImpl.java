package components.message;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.ForgivingExceptionHandler;
import components.exceptions.QueueException;
import play.inject.ApplicationLifecycle;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ConnectionManagerImpl implements ConnectionManager {

  private final Connection connection;

  @Inject
  public ConnectionManagerImpl(ApplicationLifecycle lifecycle, @Named("rabbitMqUrl") String rabbitMqUrl) {
    connection = initConnection(rabbitMqUrl);
    lifecycle.addStopHook(() -> {
      connection.close();
      return CompletableFuture.completedFuture(null);
    });
  }

  private Connection initConnection(String url) {
    try {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setUri(url);
      factory.setExceptionHandler(new ForgivingExceptionHandler());
      return factory.newConnection();
    } catch (Exception exception) {
      String message = "Unable to connect to rabbitMQ at url" + url;
      throw new QueueException(message, exception);
    }
  }

  @Override
  public Channel createChannel() {
    try {
      return connection.createChannel();
    } catch (IOException ioe) {
      throw new QueueException("Unable to create rabbitMQ channel", ioe);
    }
  }

}

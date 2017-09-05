package components.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import components.dao.RfiDao;
import components.dao.StatusUpdateDao;
import components.exceptions.DatabaseException;
import components.exceptions.ValidationException;
import components.util.EnumUtil;
import models.Rfi;
import models.StatusUpdate;
import models.enums.RoutingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Validation;
import javax.validation.Validator;

public class MessageConsumerImpl extends DefaultConsumer implements MessageConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerImpl.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

  private final StatusUpdateDao statusUpdateDao;
  private final RfiDao rfiDao;

  @Inject
  public MessageConsumerImpl(RfiDao rfiDao, StatusUpdateDao statusUpdateDao, Channel channel) {
    super(channel);
    this.rfiDao = rfiDao;
    this.statusUpdateDao = statusUpdateDao;
  }

  @Override
  public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
      throws IOException {
    String message = new String(body, "UTF-8");
    RoutingKey routingKey = EnumUtil.parse(envelope.getRoutingKey(), RoutingKey.class);
    if (routingKey == null) {
      LOGGER.error("Routing key cannot be null.");
      reject(envelope);
      return;
    }
    boolean success;
    switch (routingKey) {
      case RFI_CREATE:
        success = insertRfi(message);
        break;
      case STATUS_UPDATE:
        success = insertStatusUpdate(message);
        break;
      default:
        LOGGER.error("Unknown routing key {}", routingKey);
        success = false;
        break;
    }
    if (success) {
      acknowledge(envelope);
    } else {
      reject(envelope);
    }
  }

  private boolean insertRfi(String message) {
    Rfi rfi;
    try {
      rfi = MAPPER.readValue(message, Rfi.class);
    } catch (IOException ioe) {
      LOGGER.error("Unable to parse message into rfi {}", message);
      return false;
    }
    try {
      validate(rfi);
    } catch (ValidationException ve) {
      LOGGER.error("Rfi of message {} is not valid because {}", message, ve.getMessage());
      return false;
    }
    try {
      rfiDao.insertRfi(rfi);
    } catch (DatabaseException databaseException) {
      LOGGER.error("Unable to insert rfi for message {} ", message, databaseException);
      return false;
    }
    return true;
  }

  private boolean insertStatusUpdate(String message) {
    StatusUpdate statusUpdate;
    try {
      statusUpdate = MAPPER.readValue(message, StatusUpdate.class);
    } catch (IOException ioe) {
      LOGGER.error("Unable to parse message into status update {}", message);
      return false;
    }
    try {
      validate(statusUpdate);
    } catch (ValidationException ve) {
      LOGGER.error("StatusUpdate of message {} is not valid because {}", message, ve.getMessage());
      return false;
    }
    try {
      statusUpdateDao.insertStatusUpdate(statusUpdate);
    } catch (DatabaseException databaseException) {
      LOGGER.error("Unable to insert statusUpdate for message {} ", message, databaseException);
      return false;
    }
    return true;
  }

  private void reject(Envelope envelope) throws IOException {
    getChannel().basicReject(envelope.getDeliveryTag(), false);
  }

  private void acknowledge(Envelope envelope) throws IOException {
    getChannel().basicAck(envelope.getDeliveryTag(), false);
  }

  private void validate(Object object) {
    List<String> errorMessages = VALIDATOR.validate(object).stream()
        .map(cv -> cv.getPropertyPath().toString() + " " + cv.getMessage())
        .collect(Collectors.toList());
    if (!errorMessages.isEmpty()) {
      String errorMessage = String.join(", ", errorMessages);
      throw new ValidationException(errorMessage);
    }
  }

}

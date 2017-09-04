package components.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import components.dao.StatusUpdateDao;
import components.exceptions.DatabaseException;
import components.exceptions.ParseException;
import models.StatusUpdate;
import models.enums.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;

public class SpireRelayConsumerImpl extends DefaultConsumer implements SpireRelayConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpireRelayConsumerImpl.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final StatusUpdateDao statusUpdateDao;

  @Inject
  public SpireRelayConsumerImpl(StatusUpdateDao statusUpdateDao, Channel channel) {
    super(channel);
    this.statusUpdateDao = statusUpdateDao;
  }

  @Override
  public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
      throws IOException {
    String message = new String(body, "UTF-8");
    StatusUpdate statusUpdate;
    try {
      statusUpdate = parse(message);
    } catch (ParseException pe) {
      LOGGER.error("Unable to parse statusUpdate from message {}", message, pe);
      reject(envelope);
      return;
    }
    try {
      statusUpdateDao.insertStatusUpdate(statusUpdate);
    } catch (DatabaseException databaseException) {
      LOGGER.error("Unable to insert statusUpdate for message {} ", message, databaseException);
      reject(envelope);
      return;
    }
    acknowledge(envelope);
  }

  private void reject(Envelope envelope) throws IOException {
    getChannel().basicReject(envelope.getDeliveryTag(), false);
  }

  private void acknowledge(Envelope envelope) throws IOException {
    getChannel().basicAck(envelope.getDeliveryTag(), false);
  }

  private StatusUpdate parse(String message) {
    SpireCaseParam spireCaseParam = readMessage(message);
    Long startTimestamp = parseTimestamp(spireCaseParam);
    StatusType statusType = parseStatusCode(spireCaseParam);
    return new StatusUpdate(spireCaseParam.getLiteApplicationId(), statusType, startTimestamp, Instant.now().toEpochMilli());
  }

  private SpireCaseParam readMessage(String message) {
    try {
      return MAPPER.readValue(message, SpireCaseParam.class);
    } catch (IOException ioe) {
      throw new ParseException("Unable to parse message into SpireCaseParam", ioe);
    }
  }

  private long parseTimestamp(SpireCaseParam spireCaseParam) {
    try {
      return Long.parseLong(spireCaseParam.getTimestamp());
    } catch (NumberFormatException nfe) {
      String errorMessage = "Unable to parse timestamp " + spireCaseParam.getTimestamp();
      throw new ParseException(errorMessage, nfe);
    }
  }

  private StatusType parseStatusCode(SpireCaseParam spireCaseParam) {
    try {
      return StatusType.valueOf(spireCaseParam.getStatusCode());
    } catch (IllegalArgumentException | NullPointerException exception) {
      String errorMessage = "Unknown statusCode " + spireCaseParam.getStatusCode();
      throw new ParseException(errorMessage, exception);
    }
  }

}

package components.message;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.dao.BacklogDao;
import components.exceptions.ObjectMapperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.exporterdashboard.api.ExporterDashboardMessage;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;

public class MessagePublisherImpl implements MessagePublisher {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessagePublisherImpl.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final String awsSnsTopicArn;
  private final AmazonSNS amazonSNS;
  private final BacklogDao backlogDao;

  @Inject
  public MessagePublisherImpl(@Named("awsSnsTopicArn") String awsSnsTopicArn,
                              AmazonSNS amazonSNS,
                              BacklogDao backlogDao) {
    this.awsSnsTopicArn = awsSnsTopicArn;
    this.amazonSNS = amazonSNS;
    this.backlogDao = backlogDao;
  }

  @Override
  public void sendMessage(RoutingKey routingKey, ExporterDashboardMessage exporterDashboardMessage) {
    String message;
    try {
      message = MAPPER.writeValueAsString(exporterDashboardMessage);
    } catch (Exception exception) {
      throw new ObjectMapperException("Unable to write message as string with routingKey " + routingKey, exception);
    }
    try {
      MessageAttributeValue value = new MessageAttributeValue().withDataType("String")
          .withStringValue(routingKey.toString());
      PublishRequest publishRequest = new PublishRequest().withTopicArn(awsSnsTopicArn)
          .withMessage(message)
          .addMessageAttributesEntry("routingKey", value);
      PublishResult publishResult = amazonSNS.publish(publishRequest);
      LOGGER.info("Successfully published message with routingKey {} and messageId {}", routingKey, publishResult.getMessageId());
    } catch (Exception exception) {
      LOGGER.error("Unable to send message {} with routingKey {}", message, routingKey, exception);
      backlogDao.insert(System.currentTimeMillis(), routingKey.toString(), message);
    }
  }

}

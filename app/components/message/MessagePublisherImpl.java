package components.message;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.exporterdashboard.api.ExporterDashboardMessage;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;

public class MessagePublisherImpl implements MessagePublisher {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessagePublisherImpl.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final String awsSnsTopicArn;
  private final AmazonSNS amazonSNS;

  @Inject
  public MessagePublisherImpl(@Named("awsSnsTopicArn") String awsSnsTopicArn,
                              AmazonSNS amazonSNS) {
    this.awsSnsTopicArn = awsSnsTopicArn;
    this.amazonSNS = amazonSNS;
  }

  @Override
  public void sendMessage(RoutingKey routingKey, ExporterDashboardMessage exporterDashboardMessage) {
    String messageBody;
    try {
      messageBody = MAPPER.writeValueAsString(exporterDashboardMessage);
    } catch (Exception exception) {
      throw new RuntimeException("Unable to write message as string with routingKey " + routingKey, exception);
    }
    MessageAttributeValue value = new MessageAttributeValue().withDataType("String")
        .withStringValue(routingKey.toString());
    PublishRequest publishRequest = new PublishRequest().withTopicArn(awsSnsTopicArn)
        .withMessage(messageBody)
        .addMessageAttributesEntry("routingKey", value);
    amazonSNS.publish(publishRequest);
  }

}

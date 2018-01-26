package components.message;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.inject.ApplicationLifecycle;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SqsPollerImpl implements SqsPoller {

  private static final Logger LOGGER = LoggerFactory.getLogger(SqsPollerImpl.class);

  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  private final AmazonSQS amazonSQS;
  private final String awsSqsQueueUrl;
  private final int awsSqsWaitTimeSeconds;
  private final MessageHandler messageHandler;

  private boolean continuePolling = true;

  @Inject
  public SqsPollerImpl(@Named("awsSqsQueueUrl") String awsSqsQueueUrl,
                       @Named("awsSqsWaitTimeSeconds") int awsSqsWaitTimeSeconds,
                       ApplicationLifecycle lifecycle,
                       AmazonSQS amazonSQS,
                       MessageHandler messageHandler) {
    lifecycle.addStopHook(() -> {
      continuePolling = false;
      executor.shutdownNow();
      return CompletableFuture.completedFuture(null);
    });
    this.amazonSQS = amazonSQS;
    this.messageHandler = messageHandler;
    this.awsSqsQueueUrl = awsSqsQueueUrl;
    this.awsSqsWaitTimeSeconds = awsSqsWaitTimeSeconds;
    receiveMessages();
  }

  private void receiveMessages() {
    executor.submit(() -> {
      while (continuePolling && !Thread.interrupted()) {
        try {
          ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
          receiveMessageRequest.withMessageAttributeNames("routingKey");
          receiveMessageRequest.withQueueUrl(awsSqsQueueUrl);
          receiveMessageRequest.setWaitTimeSeconds(awsSqsWaitTimeSeconds);
          List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
          for (Message message : messages) {
            String body = message.getBody();
            MessageAttributeValue messageAttributeValue = message.getMessageAttributes().get("routingKey");
            String type;
            if (messageAttributeValue != null) {
              type = messageAttributeValue.getStringValue();
            } else {
              type = null;
            }
            boolean success = messageHandler.handleMessage(type, body);
            if (success) {
              amazonSQS.deleteMessage(awsSqsQueueUrl, message.getReceiptHandle());
            }
          }
        } catch (Exception exception) {
          LOGGER.error("An exception occurred receiving messages from sqs", exception);
          ThreadUtil.sleep(5000);
        }
      }
    });
  }

}

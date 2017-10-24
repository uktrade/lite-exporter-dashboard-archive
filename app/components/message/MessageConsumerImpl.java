package components.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import components.dao.NotificationDao;
import components.dao.OutcomeDao;
import components.dao.RfiDao;
import components.dao.RfiWithdrawalDao;
import components.dao.StatusUpdateDao;
import components.dao.WithdrawalRejectionDao;
import components.exceptions.ValidationException;
import components.util.EnumUtil;
import components.util.RandomIdUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Validation;
import javax.validation.Validator;
import models.Document;
import models.File;
import models.Notification;
import models.NotificationType;
import models.Outcome;
import models.Rfi;
import models.RfiWithdrawal;
import models.StatusUpdate;
import models.WithdrawalRejection;
import models.enums.DocumentType;
import models.enums.StatusType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.LiteCaseStatusUpdate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.LiteMessageDocument;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.LiteNotificationDelay;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.LiteNotificationInform;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.LiteNotificationStop;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.LiteOutcomeAmend;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.LiteOutcomeDocument;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.LiteOutcomeIssue;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.LiteRfiCreate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.LiteRfiWithdrawalCreate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.LiteWithdrawalReject;

public class MessageConsumerImpl extends DefaultConsumer implements MessageConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerImpl.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

  private final NotificationDao notificationDao;
  private final StatusUpdateDao statusUpdateDao;
  private final RfiDao rfiDao;
  private final RfiWithdrawalDao rfiWithdrawalDao;
  private final OutcomeDao outcomeDao;
  private final WithdrawalRejectionDao withdrawalRejectionDao;

  @Inject
  public MessageConsumerImpl(
      Channel channel,
      RfiDao rfiDao,
      StatusUpdateDao statusUpdateDao,
      NotificationDao notificationDao,
      RfiWithdrawalDao rfiWithdrawalDao,
      OutcomeDao outcomeDao,
      WithdrawalRejectionDao withdrawalRejectionDao) {
    super(channel);
    this.rfiDao = rfiDao;
    this.statusUpdateDao = statusUpdateDao;
    this.notificationDao = notificationDao;
    this.rfiWithdrawalDao = rfiWithdrawalDao;
    this.outcomeDao = outcomeDao;
    this.withdrawalRejectionDao = withdrawalRejectionDao;
  }

  @Override
  public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
      throws IOException {
    String message = new String(body, "UTF-8");
    ConsumerRoutingKey consumerRoutingKey = EnumUtil.parse(envelope.getRoutingKey(), ConsumerRoutingKey.class);
    if (consumerRoutingKey == null) {
      LOGGER.error("Routing key cannot be null.");
      reject(envelope);
      return;
    }
    try {
      switch (consumerRoutingKey) {
        case RFI:
          insertRfi(message);
          break;
        case STATUS_UPDATE:
          insertStatusUpdate(message);
          break;
        case DELAY_NOTIFICATION:
          insertDelayNotification(message);
          break;
        case STOP_NOTIFICATION:
          insertStopNotification(message);
          break;
        case INFORM_NOTIFICATION:
          insertInformNotification(message);
          break;
        case RFI_WITHDRAWAL:
          insertRfiWithdrawal(message);
          break;
        case OUTCOME_ISSUE:
          insertOutcomeIssue(message);
          break;
        case OUTCOME_AMEND:
          insertOutcomeAmend(message);
          break;
        case WITHDRAWAL_REJECTION:
          insertWithdrawalRejection(message);
          break;
        default:
          throw new ValidationException("Unknown routing key " + consumerRoutingKey);
      }
    } catch (Exception exception) {
      String errorMessage = String.format("Unable to handle delivery of message %s with routing key %s", message, consumerRoutingKey);
      LOGGER.error(errorMessage, exception);
      reject(envelope);
      return;
    }
    acknowledge(envelope);
  }

  private void insertRfi(String message) {
    LiteRfiCreate liteRfiCreate = parse(message, LiteRfiCreate.class);
    Rfi rfi = new Rfi(liteRfiCreate.getId(),
        liteRfiCreate.getAppId(),
        liteRfiCreate.getCreatedTimestamp(),
        liteRfiCreate.getDeadlineTimestamp(),
        liteRfiCreate.getSentByUserId(),
        liteRfiCreate.getRecipientUserIds(),
        liteRfiCreate.getMessage());
    validate(rfi);
    rfiDao.insertRfi(rfi);
  }

  private void insertStatusUpdate(String message) {
    LiteCaseStatusUpdate liteCaseStatusUpdate = parse(message, LiteCaseStatusUpdate.class);
    if (liteCaseStatusUpdate.getStatusCode() == null) {
      throw new ValidationException("statusCode of LiteCaseStatusUpdate cannot be null.");
    }
    StatusType statusType = EnumUtil.parse(liteCaseStatusUpdate.getStatusCode().toString(), StatusType.class);
    StatusUpdate statusUpdate = new StatusUpdate(RandomIdUtil.statusUpdateId(),
        liteCaseStatusUpdate.getAppId(),
        statusType,
        liteCaseStatusUpdate.getTimestamp());
    validate(statusUpdate);
    statusUpdateDao.insertStatusUpdate(statusUpdate);
  }

  private void insertDelayNotification(String message) {
    LiteNotificationDelay liteNotificationDelay = parse(message, LiteNotificationDelay.class);
    Notification notification = new Notification(liteNotificationDelay.getId(),
        liteNotificationDelay.getAppId(),
        NotificationType.DELAY,
        null,
        liteNotificationDelay.getSentTimestamp(),
        liteNotificationDelay.getRecipientUserIds(),
        liteNotificationDelay.getMessage(),
        null);
    validate(notification, "createdByUserId", "document");
    notificationDao.insertNotification(notification);
  }

  private void insertStopNotification(String message) {
    LiteNotificationStop liteNotificationStop = parse(message, LiteNotificationStop.class);
    Notification notification = new Notification(liteNotificationStop.getId(),
        liteNotificationStop.getAppId(),
        NotificationType.STOP,
        liteNotificationStop.getSentByUserId(),
        liteNotificationStop.getSentTimestamp(),
        liteNotificationStop.getRecipientUserIds(),
        liteNotificationStop.getMessage(),
        null);
    validate(notification, "document");
    notificationDao.insertNotification(notification);
  }

  private void insertInformNotification(String message) {
    LiteNotificationInform liteNotificationInform = parse(message, LiteNotificationInform.class);
    LiteMessageDocument liteMessageDocument = liteNotificationInform.getDocument();
    if (liteMessageDocument == null) {
      throw new ValidationException("Document of LiteNotificationInform cannot be null.");
    }
    File file = new File(liteMessageDocument.getId(), liteMessageDocument.getFilename(), liteMessageDocument.getUrl());
    validate(file);
    Notification notification = new Notification(liteNotificationInform.getId(),
        liteNotificationInform.getAppId(),
        NotificationType.INFORM,
        liteNotificationInform.getSentByUserId(),
        liteNotificationInform.getSentTimestamp(),
        liteNotificationInform.getRecipientUserIds(),
        null,
        file);
    validate(notification, "message");
    notificationDao.insertNotification(notification);
  }

  private void insertRfiWithdrawal(String message) {
    LiteRfiWithdrawalCreate liteRfiWithdrawalCreate = parse(message, LiteRfiWithdrawalCreate.class);
    RfiWithdrawal rfiWithdrawal = new RfiWithdrawal(RandomIdUtil.rfiWithdrawalId(),
        liteRfiWithdrawalCreate.getRfiId(),
        liteRfiWithdrawalCreate.getCreatedByUserId(),
        liteRfiWithdrawalCreate.getCreatedTimestamp(),
        liteRfiWithdrawalCreate.getRecipientUserIds(),
        liteRfiWithdrawalCreate.getMessage());
    validate(rfiWithdrawal);
    rfiWithdrawalDao.insertRfiWithdrawal(rfiWithdrawal);
  }

  private void insertOutcomeIssue(String message) {
    LiteOutcomeIssue liteOutcomeIssue = parse(message, LiteOutcomeIssue.class);
    List<Document> documents = parseDocuments(liteOutcomeIssue.getDocuments(), "ISSUE_");
    Outcome outcome = new Outcome(liteOutcomeIssue.getId(),
        liteOutcomeIssue.getAppId(),
        liteOutcomeIssue.getCreatedByUserId(),
        liteOutcomeIssue.getRecipientUserIds(),
        liteOutcomeIssue.getCreatedTimestamp(),
        documents);
    validate(outcome);
    outcomeDao.insertOutcome(outcome);
  }

  private void insertOutcomeAmend(String message) {
    LiteOutcomeAmend liteOutcomeAmend = parse(message, LiteOutcomeAmend.class);
    List<Document> documents = parseDocuments(liteOutcomeAmend.getDocuments(), "AMENDMENT_");
    Outcome outcome = new Outcome(liteOutcomeAmend.getId(),
        liteOutcomeAmend.getAppId(),
        liteOutcomeAmend.getCreatedByUserId(),
        liteOutcomeAmend.getRecipientUserIds(),
        liteOutcomeAmend.getCreatedTimestamp(),
        documents);
    validate(outcome);
    outcomeDao.insertOutcome(outcome);
  }

  private List<Document> parseDocuments(List<LiteOutcomeDocument> liteOutcomeDocuments, String prefix) {
    if (CollectionUtils.isEmpty(liteOutcomeDocuments) || liteOutcomeDocuments.contains(null)) {
      throw new ValidationException("Documents cannot be empty or contain null.");
    }
    List<Document> documents = liteOutcomeDocuments.stream()
        .map(liteOutcomeDocument -> {
          DocumentType documentType = EnumUtil.parse(prefix + liteOutcomeDocument.getDocumentType(), DocumentType.class);
          return new Document(liteOutcomeDocument.getId(),
              documentType,
              liteOutcomeDocument.getLicenceRef(),
              liteOutcomeDocument.getFileName(),
              liteOutcomeDocument.getUrl());
        }).collect(Collectors.toList());
    documents.forEach(document -> {
      if (document.getDocumentType() == DocumentType.AMENDMENT_LICENCE_DOCUMENT || document.getDocumentType() == DocumentType.ISSUE_LICENCE_DOCUMENT) {
        validate(document);
      } else {
        validate(document, "licenceRef");
      }
    });
    return documents;
  }

  private void insertWithdrawalRejection(String message) {
    LiteWithdrawalReject liteWithdrawalReject = parse(message, LiteWithdrawalReject.class);
    WithdrawalRejection withdrawalRejection = new WithdrawalRejection(RandomIdUtil.withdrawalRejectionId(),
        liteWithdrawalReject.getAppId(),
        liteWithdrawalReject.getRejectedByUserId(),
        System.currentTimeMillis(),
        new ArrayList<>(),
        liteWithdrawalReject.getMessage());
    validate(withdrawalRejection);
    withdrawalRejectionDao.insertWithdrawalRejection(withdrawalRejection);
  }

  private void reject(Envelope envelope) throws IOException {
    getChannel().basicReject(envelope.getDeliveryTag(), false);
  }

  private void acknowledge(Envelope envelope) throws IOException {
    getChannel().basicAck(envelope.getDeliveryTag(), false);
  }

  private <T> T parse(String message, Class<T> clazz) {
    try {
      T object = MAPPER.readValue(message, clazz);
      if (object == null) {
        throw new ValidationException("Unable to parse message into class " + clazz.getSimpleName());
      } else {
        return object;
      }
    } catch (IOException ioe) {
      throw new ValidationException("Unable to parse message into class " + clazz.getSimpleName());
    }
  }

  private void validate(Object object, String... excludeFields) {
    Set<String> exclude = Stream.of(excludeFields).collect(Collectors.toSet());
    List<String> errorMessages = VALIDATOR.validate(object).stream()
        .filter(cv -> !exclude.contains(cv.getPropertyPath().toString()))
        .map(cv -> cv.getPropertyPath().toString() + " " + cv.getMessage())
        .collect(Collectors.toList());
    if (!errorMessages.isEmpty()) {
      String errorMessage = String.join(", ", errorMessages);
      throw new ValidationException(errorMessage);
    }
  }

}

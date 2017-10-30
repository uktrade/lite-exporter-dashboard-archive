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
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardCaseStatusUpdate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardMessageDocument;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardNotificationDelay;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardNotificationInform;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardNotificationStop;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardOutcomeAmend;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardOutcomeDocument;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardOutcomeIssue;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardRfiCreate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardRfiWithdrawalCreate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardWithdrawalReject;

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
    DashboardRfiCreate dashboardRfiCreate = parse(message, DashboardRfiCreate.class);
    Rfi rfi = new Rfi(dashboardRfiCreate.getId(),
        dashboardRfiCreate.getAppId(),
        dashboardRfiCreate.getCreatedTimestamp(),
        dashboardRfiCreate.getDeadlineTimestamp(),
        dashboardRfiCreate.getCreatedByUserId(),
        dashboardRfiCreate.getRecipientUserIds(),
        dashboardRfiCreate.getMessage());
    validate(rfi);
    rfiDao.insertRfi(rfi);
  }

  private void insertStatusUpdate(String message) {
    DashboardCaseStatusUpdate dashboardCaseStatusUpdate = parse(message, DashboardCaseStatusUpdate.class);
    if (dashboardCaseStatusUpdate.getStatusCode() == null) {
      throw new ValidationException("statusCode of DashboardCaseStatusUpdate cannot be null.");
    }
    StatusType statusType = EnumUtil.parse(dashboardCaseStatusUpdate.getStatusCode().toString(), StatusType.class);
    StatusUpdate statusUpdate = new StatusUpdate(RandomIdUtil.statusUpdateId(),
        dashboardCaseStatusUpdate.getAppId(),
        statusType,
        dashboardCaseStatusUpdate.getCreatedTimestamp());
    validate(statusUpdate);
    statusUpdateDao.insertStatusUpdate(statusUpdate);
  }

  private void insertDelayNotification(String message) {
    DashboardNotificationDelay dashboardNotificationDelay = parse(message, DashboardNotificationDelay.class);
    Notification notification = new Notification(dashboardNotificationDelay.getId(),
        dashboardNotificationDelay.getAppId(),
        NotificationType.DELAY,
        null,
        dashboardNotificationDelay.getCreatedTimestamp(),
        dashboardNotificationDelay.getRecipientUserIds(),
        dashboardNotificationDelay.getMessage(),
        null);
    validate(notification, "createdByUserId", "document");
    notificationDao.insertNotification(notification);
  }

  private void insertStopNotification(String message) {
    DashboardNotificationStop dashboardNotificationStop = parse(message, DashboardNotificationStop.class);
    Notification notification = new Notification(dashboardNotificationStop.getId(),
        dashboardNotificationStop.getAppId(),
        NotificationType.STOP,
        dashboardNotificationStop.getCreatedByUserId(),
        dashboardNotificationStop.getCreatedTimestamp(),
        dashboardNotificationStop.getRecipientUserIds(),
        dashboardNotificationStop.getMessage(),
        null);
    validate(notification, "document");
    notificationDao.insertNotification(notification);
  }

  private void insertInformNotification(String message) {
    DashboardNotificationInform dashboardNotificationInform = parse(message, DashboardNotificationInform.class);
    DashboardMessageDocument dashboardMessageDocument = dashboardNotificationInform.getDocument();
    if (dashboardMessageDocument == null) {
      throw new ValidationException("Document of DashboardNotificationInform cannot be null.");
    }
    File file = new File(dashboardMessageDocument.getId(), dashboardMessageDocument.getFilename(), dashboardMessageDocument.getUrl());
    validate(file);
    Notification notification = new Notification(dashboardNotificationInform.getId(),
        dashboardNotificationInform.getAppId(),
        NotificationType.INFORM,
        dashboardNotificationInform.getCreatedByUserId(),
        dashboardNotificationInform.getCreatedTimestamp(),
        dashboardNotificationInform.getRecipientUserIds(),
        null,
        file);
    validate(notification, "message");
    notificationDao.insertNotification(notification);
  }

  private void insertRfiWithdrawal(String message) {
    DashboardRfiWithdrawalCreate dashboardRfiWithdrawalCreate = parse(message, DashboardRfiWithdrawalCreate.class);
    RfiWithdrawal rfiWithdrawal = new RfiWithdrawal(RandomIdUtil.rfiWithdrawalId(),
        dashboardRfiWithdrawalCreate.getRfiId(),
        dashboardRfiWithdrawalCreate.getCreatedByUserId(),
        dashboardRfiWithdrawalCreate.getCreatedTimestamp(),
        dashboardRfiWithdrawalCreate.getRecipientUserIds(),
        dashboardRfiWithdrawalCreate.getMessage());
    validate(rfiWithdrawal);
    rfiWithdrawalDao.insertRfiWithdrawal(rfiWithdrawal);
  }

  private void insertOutcomeIssue(String message) {
    DashboardOutcomeIssue dashboardOutcomeIssue = parse(message, DashboardOutcomeIssue.class);
    List<Document> documents = parseDocuments(dashboardOutcomeIssue.getDocuments(), "ISSUE_");
    Outcome outcome = new Outcome(dashboardOutcomeIssue.getId(),
        dashboardOutcomeIssue.getAppId(),
        dashboardOutcomeIssue.getCreatedByUserId(),
        dashboardOutcomeIssue.getRecipientUserIds(),
        dashboardOutcomeIssue.getCreatedTimestamp(),
        documents);
    validate(outcome);
    outcomeDao.insertOutcome(outcome);
  }

  private void insertOutcomeAmend(String message) {
    DashboardOutcomeAmend dashboardOutcomeAmend = parse(message, DashboardOutcomeAmend.class);
    List<Document> documents = parseDocuments(dashboardOutcomeAmend.getDocuments(), "AMENDMENT_");
    Outcome outcome = new Outcome(dashboardOutcomeAmend.getId(),
        dashboardOutcomeAmend.getAppId(),
        dashboardOutcomeAmend.getCreatedByUserId(),
        dashboardOutcomeAmend.getRecipientUserIds(),
        dashboardOutcomeAmend.getCreatedTimestamp(),
        documents);
    validate(outcome);
    outcomeDao.insertOutcome(outcome);
  }

  private List<Document> parseDocuments(List<DashboardOutcomeDocument> dashboardOutcomeDocuments, String prefix) {
    if (CollectionUtils.isEmpty(dashboardOutcomeDocuments) || dashboardOutcomeDocuments.contains(null)) {
      throw new ValidationException("Documents cannot be empty or contain null.");
    }
    List<Document> documents = dashboardOutcomeDocuments.stream()
        .map(dashboardOutcomeDocument -> {
          DocumentType documentType = EnumUtil.parse(prefix + dashboardOutcomeDocument.getDocumentType(), DocumentType.class);
          return new Document(dashboardOutcomeDocument.getId(),
              documentType,
              dashboardOutcomeDocument.getLicenceRef(),
              dashboardOutcomeDocument.getFilename(),
              dashboardOutcomeDocument.getUrl());
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
    DashboardWithdrawalReject dashboardWithdrawalReject = parse(message, DashboardWithdrawalReject.class);
    WithdrawalRejection withdrawalRejection = new WithdrawalRejection(RandomIdUtil.withdrawalRejectionId(),
        dashboardWithdrawalReject.getAppId(),
        dashboardWithdrawalReject.getCreatedByUserId(),
        System.currentTimeMillis(),
        new ArrayList<>(),
        dashboardWithdrawalReject.getMessage());
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

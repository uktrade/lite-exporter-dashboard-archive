package pact.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.MessagePactProviderRule;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.model.v3.messaging.MessagePact;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import components.dao.NotificationDao;
import components.dao.OutcomeDao;
import components.dao.RfiDao;
import components.dao.RfiWithdrawalDao;
import components.dao.StatusUpdateDao;
import components.dao.WithdrawalRejectionDao;
import components.message.ConsumerRoutingKey;
import components.message.MessageConsumer;
import components.message.MessageConsumerImpl;
import java.io.IOException;
import models.Document;
import models.Notification;
import models.NotificationType;
import models.Outcome;
import models.Rfi;
import models.RfiWithdrawal;
import models.StatusUpdate;
import models.WithdrawalRejection;
import models.enums.DocumentType;
import models.enums.StatusType;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class ConsumerPact {

  private final static String PROVIDER = "lite-spire-relay-service";
  private final static String CONSUMER = "lite-exporter-dashboard";

  private final RfiDao rfiDao = mock(RfiDao.class);
  private final RfiWithdrawalDao rfiWithdrawalDao = mock(RfiWithdrawalDao.class);
  private final StatusUpdateDao statusUpdateDao = mock(StatusUpdateDao.class);
  private final NotificationDao notificationDao = mock(NotificationDao.class);
  private final OutcomeDao outcomeDao = mock(OutcomeDao.class);
  private final WithdrawalRejectionDao withdrawalRejectionDao = mock(WithdrawalRejectionDao.class);
  private final Channel channel = mock(Channel.class);
  private final MessageConsumer messageConsumer = new MessageConsumerImpl(channel,
      rfiDao,
      statusUpdateDao,
      notificationDao,
      rfiWithdrawalDao,
      outcomeDao,
      withdrawalRejectionDao);

  @Rule
  public MessagePactProviderRule mockProvider = new MessagePactProviderRule(PROVIDER, this);

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public MessagePact createStatusUpdate(MessagePactBuilder builder) {
    PactDslJsonBody body = new PactDslJsonBody()
        .stringValue("appId", "appId")
        .stringValue("statusCode", "INITIAL_CHECKS")
        .integerType("createdTimestamp", 123456789L);
    return builder
        .expectsToReceive("a status update message with status code INITIAL_CHECKS")
        .withContent(body)
        .toPact();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public MessagePact createRfi(MessagePactBuilder builder) {
    PactDslJsonBody body = new PactDslJsonBody()
        .stringType("id", "rfiId")
        .stringType("appId", "appId")
        .stringType("createdByUserId", "createdByUserId")
        .stringType("message", "This is an rfi message.")
        .integerType("createdTimestamp", 123456789L)
        .integerType("deadlineTimestamp", 987654321L)
        .array("recipientUserIds")
        .stringType("recipient")
        .closeArray()
        .asBody();
    return builder.expectsToReceive("a rfi message")
        .withContent(body)
        .toPact();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public MessagePact createDelayNotification(MessagePactBuilder builder) {
    PactDslJsonBody body = new PactDslJsonBody()
        .stringType("id", "delayNotificationId")
        .stringType("appId", "appId")
        .stringType("message", "This is a delay notification.")
        .integerType("createdTimestamp", 123456789L)
        .array("recipientUserIds")
        .stringType("recipient")
        .closeArray()
        .asBody();
    return builder.expectsToReceive("a delay message")
        .withContent(body)
        .toPact();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public MessagePact createStopNotification(MessagePactBuilder builder) {
    PactDslJsonBody body = new PactDslJsonBody()
        .stringType("id", "stopNotificationId")
        .stringType("appId", "appId")
        .stringType("message", "This is a stop notification.")
        .stringType("createdByUserId", "createdByUserId")
        .integerType("createdTimestamp", 123456789L)
        .array("recipientUserIds")
        .stringType("recipient")
        .closeArray()
        .asBody();
    return builder.expectsToReceive("a stop message")
        .withContent(body)
        .toPact();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public MessagePact createInformNotification(MessagePactBuilder builder) {
    PactDslJsonBody body = new PactDslJsonBody()
        .stringType("id", "informNotificationId")
        .stringType("appId", "appId")
        .stringType("createdByUserId", "createdByUserId")
        .integerType("createdTimestamp", 123456789L)
        .array("recipientUserIds")
        .stringType("recipient")
        .closeArray()
        .object("document")
        .stringType("id", "fileId")
        .stringType("filename", "filename")
        .stringType("url", "url")
        .closeObject()
        .asBody();
    return builder.expectsToReceive("an inform message")
        .withContent(body)
        .toPact();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public MessagePact createRfiWithdrawal(MessagePactBuilder builder) {
    DslPart dslPart = new PactDslJsonBody()
        .stringType("rfiId", "rfiId")
        .stringType("appId", "appId")
        .stringType("createdByUserId", "createdByUserId")
        .stringType("message", "This is an rfi withdrawal.")
        .integerType("createdTimestamp", 123456789L)
        .array("recipientUserIds")
        .stringType("recipient")
        .closeArray();
    return builder.expectsToReceive("withdraw rfi")
        .withContent(dslPart)
        .toPact();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public MessagePact createOutcomeIssueLicence(MessagePactBuilder builder) {
    DslPart dslPart = new PactDslJsonBody()
        .stringType("id", "outcomeId")
        .stringType("appId", "appId")
        .stringType("createdByUserId", "createdByUserId")
        .integerType("createdTimestamp", 123456789L)
        .array("recipientUserIds")
        .stringType("recipient")
        .closeArray()
        .array("documents")
        .object()
        .stringType("id", "documentId")
        .stringType("documentType", "LICENCE_DOCUMENT")
        .stringType("licenceRef", "licenceRef")
        .stringType("filename", "filename")
        .stringType("url", "url")
        .closeObject()
        .closeArray();
    return builder.expectsToReceive("issue of an outcome")
        .withContent(dslPart)
        .toPact();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public MessagePact createOutcomeAmendLicence(MessagePactBuilder builder) {
    DslPart dslPart = new PactDslJsonBody()
        .stringType("id", "outcomeId")
        .stringType("appId", "appId")
        .stringType("createdByUserId", "createdByUserId")
        .integerType("createdTimestamp", 123456789L)
        .array("recipientUserIds")
        .stringType("recipient")
        .closeArray()
        .array("documents")
        .object()
        .stringType("id", "documentId")
        .stringType("documentType", "LICENCE_DOCUMENT")
        .stringType("licenceRef", "licenceRef")
        .stringType("filename", "filename")
        .stringType("url", "url")
        .closeObject()
        .closeArray();
    return builder.expectsToReceive("amend an outcome")
        .withContent(dslPart)
        .toPact();
  }

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public MessagePact createWithdrawalRejection(MessagePactBuilder builder) {
    DslPart dslPart = new PactDslJsonBody()
        .stringType("appId", "appId")
        .stringType("createdByUserId", "createdByUserId")
        .stringType("message", "This is a withdrawal rejection.");
    return builder.expectsToReceive("reject withdrawal request")
        .withContent(dslPart)
        .toPact();
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "createStatusUpdate")
  public void receiveStatusUpdate() throws Exception {
    handleDelivery(ConsumerRoutingKey.STATUS_UPDATE);
    verify(channel).basicAck(0, false);
    ArgumentCaptor<StatusUpdate> captor = ArgumentCaptor.forClass(StatusUpdate.class);
    verify(statusUpdateDao).insertStatusUpdate(captor.capture());

    StatusUpdate statusUpdate = captor.getValue();
    assertThat(statusUpdate.getId()).isNotEmpty();
    assertThat(statusUpdate.getAppId()).isEqualTo("appId");
    assertThat(statusUpdate.getStatusType()).isEqualTo(StatusType.INITIAL_CHECKS);
    assertThat(statusUpdate.getCreatedTimestamp()).isEqualTo(123456789L);

  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "createRfi")
  public void receiveRfi() throws Exception {
    handleDelivery(ConsumerRoutingKey.RFI);
    verify(channel).basicAck(0, false);
    ArgumentCaptor<Rfi> captor = ArgumentCaptor.forClass(Rfi.class);
    verify(rfiDao).insertRfi(captor.capture());

    Rfi rfi = captor.getValue();
    assertThat(rfi.getId()).isEqualTo("rfiId");
    assertThat(rfi.getAppId()).isEqualTo("appId");
    assertThat(rfi.getCreatedTimestamp()).isEqualTo(123456789L);
    assertThat(rfi.getDueTimestamp()).isEqualTo(987654321L);
    assertThat(rfi.getCreatedByUserId()).isEqualTo("createdByUserId");
    assertThat(rfi.getRecipientUserIds()).containsExactly("recipient");
    assertThat(rfi.getMessage()).isEqualTo("This is an rfi message.");
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "createDelayNotification")
  public void receiveDelayNotification() throws IOException {
    handleDelivery(ConsumerRoutingKey.DELAY_NOTIFICATION);
    verify(channel).basicAck(0, false);
    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationDao).insertNotification(captor.capture());

    Notification notification = captor.getValue();
    assertThat(notification.getId()).isEqualTo("delayNotificationId");
    assertThat(notification.getAppId()).isEqualTo("appId");
    assertThat(notification.getNotificationType()).isEqualTo(NotificationType.DELAY);
    assertThat(notification.getCreatedByUserId()).isNull();
    assertThat(notification.getCreatedTimestamp()).isEqualTo(123456789L);
    assertThat(notification.getRecipientUserIds()).containsExactly("recipient");
    assertThat(notification.getMessage()).isEqualTo("This is a delay notification.");
    assertThat(notification.getDocument()).isNull();
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "createStopNotification")
  public void receiveStopNotification() throws IOException {
    handleDelivery(ConsumerRoutingKey.STOP_NOTIFICATION);
    verify(channel).basicAck(0, false);
    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationDao).insertNotification(captor.capture());

    Notification notification = captor.getValue();
    assertThat(notification.getId()).isEqualTo("stopNotificationId");
    assertThat(notification.getAppId()).isEqualTo("appId");
    assertThat(notification.getNotificationType()).isEqualTo(NotificationType.STOP);
    assertThat(notification.getCreatedByUserId()).isEqualTo("createdByUserId");
    assertThat(notification.getCreatedTimestamp()).isEqualTo(123456789L);
    assertThat(notification.getRecipientUserIds()).containsExactly("recipient");
    assertThat(notification.getMessage()).isEqualTo("This is a stop notification.");
    assertThat(notification.getDocument()).isNull();
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "createInformNotification")
  public void receiveInformNotification() throws IOException {
    handleDelivery(ConsumerRoutingKey.INFORM_NOTIFICATION);
    verify(channel).basicAck(0, false);
    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationDao).insertNotification(captor.capture());

    Notification notification = captor.getValue();
    assertThat(notification.getId()).isEqualTo("informNotificationId");
    assertThat(notification.getAppId()).isEqualTo("appId");
    assertThat(notification.getNotificationType()).isEqualByComparingTo(NotificationType.INFORM);
    assertThat(notification.getCreatedByUserId()).isEqualTo("createdByUserId");
    assertThat(notification.getCreatedTimestamp()).isEqualTo(123456789L);
    assertThat(notification.getRecipientUserIds()).containsExactly("recipient");
    assertThat(notification.getMessage()).isNull();
    assertThat(notification.getDocument().getId()).isEqualTo("fileId");
    assertThat(notification.getDocument().getFilename()).isEqualTo("filename");
    assertThat(notification.getDocument().getUrl()).isEqualTo("url");
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "createRfiWithdrawal")
  public void receiveRfiWithdrawal() throws Exception {
    handleDelivery(ConsumerRoutingKey.RFI_WITHDRAWAL);
    verify(channel).basicAck(0, false);
    ArgumentCaptor<RfiWithdrawal> captor = ArgumentCaptor.forClass(RfiWithdrawal.class);
    verify(rfiWithdrawalDao).insertRfiWithdrawal(captor.capture());

    RfiWithdrawal rfiWithdrawal = captor.getValue();
    assertThat(rfiWithdrawal.getId()).isNotEmpty();
    assertThat(rfiWithdrawal.getRfiId()).isEqualTo("rfiId");
    assertThat(rfiWithdrawal.getCreatedByUserId()).isEqualTo("createdByUserId");
    assertThat(rfiWithdrawal.getCreatedTimestamp()).isEqualTo(123456789L);
    assertThat(rfiWithdrawal.getRecipientUserIds()).containsExactly("recipient");
    assertThat(rfiWithdrawal.getMessage()).isEqualTo("This is an rfi withdrawal.");

  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "createOutcomeIssueLicence")
  public void receiveOutcomeIssueLicence() throws Exception {
    handleDelivery(ConsumerRoutingKey.OUTCOME_ISSUE);
    verify(channel).basicAck(0, false);
    ArgumentCaptor<Outcome> captor = ArgumentCaptor.forClass(Outcome.class);
    verify(outcomeDao).insertOutcome(captor.capture());

    Outcome outcome = captor.getValue();
    assertThat(outcome.getId()).isEqualTo("outcomeId");
    assertThat(outcome.getAppId()).isEqualTo("appId");
    assertThat(outcome.getCreatedByUserId()).isEqualTo("createdByUserId");
    assertThat(outcome.getRecipientUserIds()).containsExactly("recipient");
    assertThat(outcome.getCreatedTimestamp()).isEqualTo(123456789L);
    assertThat(outcome.getDocuments()).hasSize(1);
    Document document = outcome.getDocuments().get(0);
    assertThat(document.getId()).isEqualTo("documentId");
    assertThat(document.getDocumentType()).isEqualTo(DocumentType.ISSUE_LICENCE_DOCUMENT);
    assertThat(document.getLicenceRef()).isEqualTo("licenceRef");
    assertThat(document.getFilename()).isEqualTo("filename");
    assertThat(document.getUrl()).isEqualTo("url");
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "createOutcomeAmendLicence")
  public void receiveOutcomeAmendLicence() throws Exception {
    handleDelivery(ConsumerRoutingKey.OUTCOME_AMEND);
    verify(channel).basicAck(0, false);
    ArgumentCaptor<Outcome> captor = ArgumentCaptor.forClass(Outcome.class);
    verify(outcomeDao).insertOutcome(captor.capture());

    Outcome outcome = captor.getValue();
    assertThat(outcome.getId()).isEqualTo("outcomeId");
    assertThat(outcome.getAppId()).isEqualTo("appId");
    assertThat(outcome.getCreatedByUserId()).isEqualTo("createdByUserId");
    assertThat(outcome.getRecipientUserIds()).containsExactly("recipient");
    assertThat(outcome.getCreatedTimestamp()).isEqualTo(123456789L);
    assertThat(outcome.getDocuments()).hasSize(1);
    Document document = outcome.getDocuments().get(0);
    assertThat(document.getId()).isEqualTo("documentId");
    assertThat(document.getDocumentType()).isEqualTo(DocumentType.AMENDMENT_LICENCE_DOCUMENT);
    assertThat(document.getLicenceRef()).isEqualTo("licenceRef");
    assertThat(document.getFilename()).isEqualTo("filename");
    assertThat(document.getUrl()).isEqualTo("url");
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "createWithdrawalRejection")
  public void receiveWithdrawalRejection() throws Exception {
    handleDelivery(ConsumerRoutingKey.WITHDRAWAL_REJECTION);
    verify(channel).basicAck(0, false);
    ArgumentCaptor<WithdrawalRejection> captor = ArgumentCaptor.forClass(WithdrawalRejection.class);
    verify(withdrawalRejectionDao).insertWithdrawalRejection(captor.capture());

    WithdrawalRejection withdrawalRejection = captor.getValue();
    assertThat(withdrawalRejection.getId()).isNotEmpty();
    assertThat(withdrawalRejection.getAppId()).isEqualTo("appId");
    assertThat(withdrawalRejection.getCreatedByUserId()).isEqualTo("createdByUserId");
    assertThat(withdrawalRejection.getMessage()).isEqualTo("This is a withdrawal rejection.");
  }

  private void handleDelivery(ConsumerRoutingKey consumerRoutingKey) throws IOException {
    Envelope envelope = mock(Envelope.class);
    when(envelope.getRoutingKey()).thenReturn(consumerRoutingKey.toString());
    messageConsumer.handleDelivery(null, envelope, null, mockProvider.getMessage());
  }

}

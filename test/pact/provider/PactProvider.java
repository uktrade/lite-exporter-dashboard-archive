package pact.provider;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.AmqpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import components.dao.AmendmentRequestDao;
import components.dao.DraftFileDao;
import components.dao.RfiReplyDao;
import components.dao.WithdrawalRequestDao;
import components.message.MessagePublisher;
import components.message.MessagePublisherImpl;
import components.service.AmendmentService;
import components.service.AmendmentServiceImpl;
import components.service.ReadMessageService;
import components.service.ReadMessageServiceImpl;
import components.service.RfiReplyService;
import components.service.RfiReplyServiceImpl;
import components.service.WithdrawalRequestService;
import components.service.WithdrawalRequestServiceImpl;
import models.Attachment;
import models.enums.DraftType;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import uk.gov.bis.lite.exporterdashboard.api.AmendmentMessage;
import uk.gov.bis.lite.exporterdashboard.api.NotificationReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.OutcomeReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.RfiReplyMessage;
import uk.gov.bis.lite.exporterdashboard.api.RfiWithdrawalReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequestAcceptReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequestMessage;

import java.util.Collections;
import java.util.List;

@RunWith(PactRunner.class)
@Provider("lite-exporter-dashboard")
@PactBroker(host = "pact-broker.ci.uktrade.io", port = "80")
public class PactProvider {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final MessagePublisher messagePublisher = mock(MessagePublisherImpl.class);

  @TestTarget
  public final Target target = new AmqpTarget(Collections.singletonList("pact.provider.*"));

  @PactVerifyProvider("an rfi was replied to")
  public String verifyRfiReplyMessage() throws JsonProcessingException {
    DraftFileDao draftFileDao = mock(DraftFileDao.class);
    when(draftFileDao.getAttachments("rfiId", DraftType.RFI_REPLY)).thenReturn(createDraftFiles());

    RfiReplyService rfiReplyService = new RfiReplyServiceImpl(mock(RfiReplyDao.class), draftFileDao, messagePublisher);
    rfiReplyService.insertRfiReply("createdByUserId", "appId", "rfiId", "message");

    ArgumentCaptor<RfiReplyMessage> captor = ArgumentCaptor.forClass(RfiReplyMessage.class);
    verify(messagePublisher).sendMessage(eq(RoutingKey.RFI_REPLY), captor.capture());

    return MAPPER.writeValueAsString(captor.getValue());
  }

  @PactVerifyProvider("a withdrawal was requested")
  public String verifyWithdrawalRequestMessage() throws JsonProcessingException {
    DraftFileDao draftFileDao = mock(DraftFileDao.class);
    when(draftFileDao.getAttachments("appId", DraftType.AMENDMENT_OR_WITHDRAWAL)).thenReturn(createDraftFiles());

    WithdrawalRequestService withdrawalRequestService = new WithdrawalRequestServiceImpl(mock(WithdrawalRequestDao.class), messagePublisher, draftFileDao);
    withdrawalRequestService.insertWithdrawalRequest("createdByUserId", "appId", "message");

    ArgumentCaptor<WithdrawalRequestMessage> captor = ArgumentCaptor.forClass(WithdrawalRequestMessage.class);
    verify(messagePublisher).sendMessage(eq(RoutingKey.WITHDRAWAL_REQUEST_CREATE), captor.capture());

    return MAPPER.writeValueAsString(captor.getValue());
  }

  @PactVerifyProvider("an application was amended")
  public String verifyAmendmentMessage() throws JsonProcessingException {
    DraftFileDao draftFileDao = mock(DraftFileDao.class);
    when(draftFileDao.getAttachments("appId", DraftType.AMENDMENT_OR_WITHDRAWAL)).thenReturn(createDraftFiles());

    AmendmentService amendmentService = new AmendmentServiceImpl(mock(AmendmentRequestDao.class), messagePublisher, draftFileDao);
    amendmentService.insertAmendment("createdByUserId", "appId", "message");

    ArgumentCaptor<AmendmentMessage> captor = ArgumentCaptor.forClass(AmendmentMessage.class);
    verify(messagePublisher).sendMessage(eq(RoutingKey.AMENDMENT_CREATE), captor.capture());

    return MAPPER.writeValueAsString(captor.getValue());
  }

  @PactVerifyProvider("an rfi withdrawal was read")
  public String verifyRfiWithdrawalReadMessage() throws JsonProcessingException {
    ReadMessageService readMessageService = new ReadMessageServiceImpl(messagePublisher);
    readMessageService.sendRfiWithdrawalReadMessage("createdByUserId", "appId", "rfiId");

    ArgumentCaptor<RfiWithdrawalReadMessage> captor = ArgumentCaptor.forClass(RfiWithdrawalReadMessage.class);
    verify(messagePublisher).sendMessage(eq(RoutingKey.RFI_WITHDRAWAL_READ), captor.capture());

    return MAPPER.writeValueAsString(captor.getValue());
  }

  @PactVerifyProvider("an outcome was read")
  public String verifyOutcomeReadMessage() throws JsonProcessingException {
    ReadMessageService readMessageService = new ReadMessageServiceImpl(messagePublisher);
    readMessageService.sendOutcomeReadMessage("createdByUserId", "appId", "outcomeId");

    ArgumentCaptor<OutcomeReadMessage> captor = ArgumentCaptor.forClass(OutcomeReadMessage.class);
    verify(messagePublisher).sendMessage(eq(RoutingKey.OUTCOME_READ), captor.capture());

    return MAPPER.writeValueAsString(captor.getValue());
  }

  @PactVerifyProvider("a notification was read")
  public String verifyInformNotificationReadMessage() throws JsonProcessingException {
    ReadMessageService readMessageService = new ReadMessageServiceImpl(messagePublisher);
    readMessageService.sendNotificationReadMessage("createdByUserId", "appId", "notificationId");

    ArgumentCaptor<NotificationReadMessage> captor = ArgumentCaptor.forClass(NotificationReadMessage.class);
    verify(messagePublisher).sendMessage(eq(RoutingKey.NOTIFICATION_READ), captor.capture());

    return MAPPER.writeValueAsString(captor.getValue());
  }

  @PactVerifyProvider("a withdrawal request accept was read")
  public String verifyWithdrawalRequestAcceptReadMessage() throws JsonProcessingException {
    ReadMessageService readMessageService = new ReadMessageServiceImpl(messagePublisher);
    readMessageService.sendWithdrawalRequestAcceptReadMessage("createdByUserId", "appId", "notificationId");

    ArgumentCaptor<WithdrawalRequestAcceptReadMessage> captor = ArgumentCaptor.forClass(WithdrawalRequestAcceptReadMessage.class);
    verify(messagePublisher).sendMessage(eq(RoutingKey.WITHDRAWAL_REQUEST_ACCEPT_READ), captor.capture());

    return MAPPER.writeValueAsString(captor.getValue());
  }

  private List<Attachment> createDraftFiles() {
    Attachment attachment = new Attachment("fileId", "filename", "bucket", "folder", 123L);
    return Collections.singletonList(attachment);
  }

}

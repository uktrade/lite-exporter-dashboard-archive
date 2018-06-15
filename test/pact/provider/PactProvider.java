package pact.provider;

//@RunWith(PactRunner.class)
//@Provider("lite-exporter-dashboard")
//@PactBroker(host = "pact-broker.ci.uktrade.io", port = "80")
public class PactProvider {
//
//  private static class MessagePublisherMock implements MessagePublisher {
//
//    private ExporterDashboardMessage lastMessage;
//
//    @Override
//    public void sendMessage(RoutingKey routingKey, ExporterDashboardMessage exporterDashboardMessage) {
//      lastMessage = exporterDashboardMessage;
//    }
//
//    ExporterDashboardMessage getLastMessage() {
//      return lastMessage;
//    }
//
//  }
//
//  private static final ObjectMapper MAPPER = new ObjectMapper();
//
//  private final MessagePublisherMock messagePublisher = new MessagePublisherMock();
//
//  @TestTarget
//  public final Target target = new AmqpTarget(Collections.singletonList("pact.provider.*"));
//
//  @PactVerifyProvider("an rfi was replied to")
//  public String verifyRfiReplyMessage() throws JsonProcessingException {
//    DraftFileDao draftFileDao = mock(DraftFileDao.class);
//    when(draftFileDao.getAttachments("rfiId", DraftType.RFI_REPLY)).thenReturn(createDraftFiles());
//
//    RfiReplyService rfiReplyService = new RfiReplyServiceImpl(mock(RfiReplyDao.class), draftFileDao, messagePublisher);
//    rfiReplyService.insertRfiReply("createdByUserId", "appId", "rfiId", "message");
//
//    return MAPPER.writeValueAsString(messagePublisher.getLastMessage());
//  }
//
//  @PactVerifyProvider("a withdrawal was requested")
//  public String verifyWithdrawalRequestMessage() throws JsonProcessingException {
//    DraftFileDao draftFileDao = mock(DraftFileDao.class);
//    when(draftFileDao.getAttachments("appId", DraftType.AMENDMENT_OR_WITHDRAWAL)).thenReturn(createDraftFiles());
//
//    WithdrawalRequestService withdrawalRequestService = new WithdrawalRequestServiceImpl(mock(WithdrawalRequestDao.class), messagePublisher, draftFileDao);
//    withdrawalRequestService.insertWithdrawalRequest("createdByUserId", "appId", "message");
//
//    return MAPPER.writeValueAsString(messagePublisher.getLastMessage());
//  }
//
//  @PactVerifyProvider("an application was amended")
//  public String verifyAmendmentMessage() throws JsonProcessingException {
//    DraftFileDao draftFileDao = mock(DraftFileDao.class);
//    when(draftFileDao.getAttachments("appId", DraftType.AMENDMENT_OR_WITHDRAWAL)).thenReturn(createDraftFiles());
//
//    AmendmentService amendmentService = new AmendmentServiceImpl(mock(AmendmentRequestDao.class), messagePublisher, draftFileDao);
//    amendmentService.insertAmendment("createdByUserId", "appId", "message");
//
//    return MAPPER.writeValueAsString(messagePublisher.getLastMessage());
//  }
//
//  @PactVerifyProvider("an rfi withdrawal was read")
//  public String verifyRfiWithdrawalReadMessage() throws JsonProcessingException {
//    ReadMessageService readMessageService = new ReadMessageServiceImpl(messagePublisher);
//    readMessageService.sendRfiWithdrawalReadMessage("createdByUserId", "appId", "rfiId");
//
//    return MAPPER.writeValueAsString(messagePublisher.getLastMessage());
//  }
//
//  @PactVerifyProvider("an outcome was read")
//  public String verifyOutcomeReadMessage() throws JsonProcessingException {
//    ReadMessageService readMessageService = new ReadMessageServiceImpl(messagePublisher);
//    readMessageService.sendOutcomeReadMessage("createdByUserId", "appId", "outcomeId");
//
//    return MAPPER.writeValueAsString(messagePublisher.getLastMessage());
//  }
//
//  @PactVerifyProvider("a notification was read")
//  public String verifyInformNotificationReadMessage() throws JsonProcessingException {
//    ReadMessageService readMessageService = new ReadMessageServiceImpl(messagePublisher);
//    readMessageService.sendNotificationReadMessage("createdByUserId", "appId", "notificationId");
//
//    return MAPPER.writeValueAsString(messagePublisher.getLastMessage());
//  }
//
//  @PactVerifyProvider("a withdrawal request accept was read")
//  public String verifyWithdrawalRequestAcceptReadMessage() throws JsonProcessingException {
//    ReadMessageService readMessageService = new ReadMessageServiceImpl(messagePublisher);
//    readMessageService.sendWithdrawalRequestAcceptReadMessage("createdByUserId", "appId", "notificationId");
//
//    return MAPPER.writeValueAsString(messagePublisher.getLastMessage());
//  }
//
//  private List<Attachment> createDraftFiles() {
//    Attachment attachment = new Attachment("fileId", "filename", "bucket", "folder", 123L);
//    return Collections.singletonList(attachment);
//  }
//
}

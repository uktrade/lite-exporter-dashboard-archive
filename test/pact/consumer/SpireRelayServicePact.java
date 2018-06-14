package pact.consumer;

public class SpireRelayServicePact {
//
//  private final static String PROVIDER = "lite-spire-relay-service";
//  private final static String CONSUMER = "lite-exporter-dashboard";
//
//  private final RfiDao rfiDao = mock(RfiDao.class);
//  private final RfiWithdrawalDao rfiWithdrawalDao = mock(RfiWithdrawalDao.class);
//  private final StatusUpdateDao statusUpdateDao = mock(StatusUpdateDao.class);
//  private final NotificationDao notificationDao = mock(NotificationDao.class);
//  private final OutcomeDao outcomeDao = mock(OutcomeDao.class);
//  private final WithdrawalRejectionDao withdrawalRejectionDao = mock(WithdrawalRejectionDao.class);
//  private final CaseDetailsDao caseDetailsDao = mock(CaseDetailsDao.class);
//  private final ApplicationDao applicationDao = mock(ApplicationDao.class);
//  private final WithdrawalApprovalDao withdrawalApprovalDao = mock(WithdrawalApprovalDao.class);
//  private final MessageHandler messageHandler = new MessageHandlerImpl(rfiDao,
//      statusUpdateDao,
//      notificationDao,
//      rfiWithdrawalDao,
//      outcomeDao,
//      withdrawalRejectionDao,
//      withdrawalApprovalDao,
//      caseDetailsDao,
//      applicationDao,
//      new EscapeHtmlServiceImpl());
//
//  @Rule
//  public MessagePactProviderRule mockProvider = new MessagePactProviderRule(PROVIDER, this);
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createStatusUpdate(MessagePactBuilder builder) {
//    PactDslJsonBody body = new PactDslJsonBody()
//        .stringType("appId", "appId")
//        .stringType("caseRef", "caseRef")
//        .stringType("statusCode", "INITIAL_CHECKS")
//        .integerType("createdTimestamp", 123456789L);
//    return builder.expectsToReceive("a status update message with status code INITIAL_CHECKS")
//        .withContent(body)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createRfi(MessagePactBuilder builder) {
//    PactDslJsonBody body = new PactDslJsonBody()
//        .stringType("id", "rfiId")
//        .stringType("appId", "appId")
//        .stringType("caseRef", "caseRef")
//        .stringType("createdByUserId", "createdByUserId")
//        .stringType("message", "This is an rfi message.")
//        .integerType("createdTimestamp", 123456789L)
//        .integerType("deadlineTimestamp", 987654321L)
//        .array("recipientUserIds")
//        .stringType("recipient")
//        .closeArray()
//        .asBody();
//    return builder.expectsToReceive("an rfi message")
//        .withContent(body)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createDelayNotification(MessagePactBuilder builder) {
//    PactDslJsonBody body = new PactDslJsonBody()
//        .stringType("id", "delayNotificationId")
//        .stringType("appId", "appId")
//        .stringType("caseRef", "caseRef")
//        .stringType("message", "This is a delay notification.")
//        .integerType("createdTimestamp", 123456789L)
//        .array("recipientUserIds")
//        .stringType("recipient")
//        .closeArray()
//        .asBody();
//    return builder.expectsToReceive("a delay message")
//        .withContent(body)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createStopNotification(MessagePactBuilder builder) {
//    PactDslJsonBody body = new PactDslJsonBody()
//        .stringType("id", "stopNotificationId")
//        .stringType("appId", "appId")
//        .stringType("caseRef", "caseRef")
//        .stringType("message", "This is a stop notification.")
//        .stringType("createdByUserId", "createdByUserId")
//        .integerType("createdTimestamp", 123456789L)
//        .array("recipientUserIds")
//        .stringType("recipient")
//        .closeArray()
//        .asBody();
//    return builder.expectsToReceive("a stop message")
//        .withContent(body)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createInformNotification(MessagePactBuilder builder) {
//    PactDslJsonBody body = new PactDslJsonBody()
//        .stringType("id", "informNotificationId")
//        .stringType("appId", "appId")
//        .stringType("caseRef", "caseRef")
//        .stringType("createdByUserId", "createdByUserId")
//        .integerType("createdTimestamp", 123456789L)
//        .array("recipientUserIds")
//        .stringType("recipient")
//        .closeArray()
//        .object("document")
//        .stringType("id", "fileId")
//        .stringType("filename", "filename")
//        .stringType("url", "url")
//        .closeObject()
//        .asBody();
//    return builder.expectsToReceive("an inform message")
//        .withContent(body)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createRfiWithdrawal(MessagePactBuilder builder) {
//    DslPart dslPart = new PactDslJsonBody()
//        .stringType("rfiId", "rfiId")
//        .stringType("appId", "appId")
//        .stringType("createdByUserId", "createdByUserId")
//        .stringType("message", "This is an rfi withdrawal.")
//        .integerType("createdTimestamp", 123456789L)
//        .array("recipientUserIds")
//        .stringType("recipient")
//        .closeArray();
//    return builder.expectsToReceive("an rfi withdrawal")
//        .withContent(dslPart)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createOutcomeIssueLicence(MessagePactBuilder builder) {
//    DslPart dslPart = new PactDslJsonBody()
//        .stringType("id", "outcomeId")
//        .stringType("appId", "appId")
//        .stringType("caseRef", "caseRef")
//        .stringType("createdByUserId", "createdByUserId")
//        .integerType("createdTimestamp", 123456789L)
//        .array("recipientUserIds")
//        .stringType("recipient")
//        .closeArray()
//        .array("documents")
//        .object()
//        .stringType("id", "documentId")
//        .stringType("documentType", "LICENCE_DOCUMENT")
//        .stringType("licenceRef", "licenceRef")
//        .stringType("licenceExpiry", "2018-01-04")
//        .stringType("filename", "filename")
//        .stringType("url", "url")
//        .closeObject()
//        .closeArray();
//    return builder.expectsToReceive("issue of an outcome")
//        .withContent(dslPart)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createOutcomeAmendLicence(MessagePactBuilder builder) {
//    DslPart dslPart = new PactDslJsonBody()
//        .stringType("id", "outcomeId")
//        .stringType("appId", "appId")
//        .stringType("caseRef", "caseRef")
//        .stringType("createdByUserId", "createdByUserId")
//        .integerType("createdTimestamp", 123456789L)
//        .array("recipientUserIds")
//        .stringType("recipient")
//        .closeArray()
//        .array("documents")
//        .object()
//        .stringType("id", "documentId")
//        .stringType("documentType", "LICENCE_DOCUMENT")
//        .stringType("licenceRef", "licenceRef")
//        .stringType("licenceExpiry", "2018-01-04")
//        .stringType("filename", "filename")
//        .stringType("url", "url")
//        .closeObject()
//        .closeArray();
//    return builder.expectsToReceive("amend an outcome")
//        .withContent(dslPart)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createWithdrawalAcceptance(MessagePactBuilder builder) {
//    DslPart dslPart = new PactDslJsonBody()
//        .stringType("id", "withdrawalAcceptId")
//        .stringType("appId", "appId")
//        .stringType("caseRef", "caseRef")
//        .integerType("createdTimestamp", 123456789L)
//        .stringType("createdByUserId", "createdByUserId")
//        .stringType("message", "This is a withdrawal acceptance.")
//        .array("recipientUserIds")
//        .stringType("recipient")
//        .closeArray();
//    return builder.expectsToReceive("a withdrawal acceptance")
//        .withContent(dslPart)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createWithdrawalRejection(MessagePactBuilder builder) {
//    DslPart dslPart = new PactDslJsonBody()
//        .stringType("appId", "appId")
//        .stringType("createdByUserId", "createdByUserId")
//        .stringType("message", "This is a withdrawal rejection.")
//        .array("recipientUserIds")
//        .stringType("recipient")
//        .closeArray();
//    return builder.expectsToReceive("a withdrawal rejection")
//        .withContent(dslPart)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createCase(MessagePactBuilder builder) {
//    DslPart dslPart = new PactDslJsonBody()
//        .stringType("appId", "appId")
//        .stringType("caseRef", "caseRef")
//        .stringType("createdByUserId", "createdByUserId")
//        .integerType("createdTimestamp", 123456789L);
//
//    return builder.expectsToReceive("case created")
//        .withContent(dslPart)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createCaseOfficerUpdate(MessagePactBuilder builder) {
//    DslPart dslPart = new PactDslJsonBody()
//        .stringType("appId", "appId")
//        .stringType("caseRef", "caseRef")
//        .stringType("caseOfficerId", "caseOfficerId")
//        .integerType("createdTimestamp", 123456789L);
//
//    return builder.expectsToReceive("case officer update")
//        .withContent(dslPart)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createRfiDeadlineUpdate(MessagePactBuilder builder) {
//    DslPart dslPart = new PactDslJsonBody()
//        .stringType("appId", "appId")
//        .stringType("rfiId", "rfiId")
//        .stringType("createdByUserId", "createdByUserId")
//        .integerType("updatedDeadlineTimestamp", 123456789L);
//
//    return builder.expectsToReceive("an rfi deadline update")
//        .withContent(dslPart)
//        .toPact();
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createStatusUpdate")
//  public void receiveStatusUpdate() throws Exception {
//    boolean success = handleDelivery(ConsumerRoutingKey.STATUS_UPDATE);
//    assertTrue(success);
//
//    ArgumentCaptor<StatusUpdate> captor = ArgumentCaptor.forClass(StatusUpdate.class);
//    verify(statusUpdateDao).insertStatusUpdate(captor.capture());
//
//    StatusUpdate statusUpdate = captor.getValue();
//    assertThat(statusUpdate.getId()).isNotEmpty();
//    assertThat(statusUpdate.getAppId()).isEqualTo("appId");
//    assertThat(statusUpdate.getStatusType()).isEqualTo(StatusType.INITIAL_CHECKS);
//    assertThat(statusUpdate.getCreatedTimestamp()).isEqualTo(123456789L);
//
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createRfi")
//  public void receiveRfi() throws Exception {
//    boolean success = handleDelivery(ConsumerRoutingKey.RFI);
//    assertTrue(success);
//
//    ArgumentCaptor<Rfi> captor = ArgumentCaptor.forClass(Rfi.class);
//    verify(rfiDao).insertRfi(captor.capture());
//
//    Rfi rfi = captor.getValue();
//    assertThat(rfi.getId()).isEqualTo("rfiId");
//    assertThat(rfi.getCaseReference()).isEqualTo("caseRef");
//    assertThat(rfi.getCreatedTimestamp()).isEqualTo(123456789L);
//    assertThat(rfi.getDueTimestamp()).isEqualTo(987654321L);
//    assertThat(rfi.getCreatedByUserId()).isEqualTo("createdByUserId");
//    assertThat(rfi.getRecipientUserIds()).containsExactly("recipient");
//    assertThat(rfi.getMessage()).isEqualTo("This is an rfi message.");
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createDelayNotification")
//  public void receiveDelayNotification() throws IOException {
//    boolean success = handleDelivery(ConsumerRoutingKey.DELAY_NOTIFICATION);
//    assertTrue(success);
//
//    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
//    verify(notificationDao).insertNotification(captor.capture());
//
//    Notification notification = captor.getValue();
//    assertThat(notification.getId()).isEqualTo("delayNotificationId");
//    assertThat(notification.getCaseReference()).isEqualTo("caseRef");
//    assertThat(notification.getNotificationType()).isEqualTo(NotificationType.DELAY);
//    assertThat(notification.getCreatedByUserId()).isNull();
//    assertThat(notification.getCreatedTimestamp()).isEqualTo(123456789L);
//    assertThat(notification.getRecipientUserIds()).containsExactly("recipient");
//    assertThat(notification.getMessage()).isEqualTo("This is a delay notification.");
//    assertThat(notification.getDocument()).isNull();
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createStopNotification")
//  public void receiveStopNotification() throws IOException {
//    boolean success = handleDelivery(ConsumerRoutingKey.STOP_NOTIFICATION);
//    assertTrue(success);
//
//    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
//    verify(notificationDao).insertNotification(captor.capture());
//
//    Notification notification = captor.getValue();
//    assertThat(notification.getId()).isEqualTo("stopNotificationId");
//    assertThat(notification.getCaseReference()).isEqualTo("caseRef");
//    assertThat(notification.getNotificationType()).isEqualTo(NotificationType.STOP);
//    assertThat(notification.getCreatedByUserId()).isEqualTo("createdByUserId");
//    assertThat(notification.getCreatedTimestamp()).isEqualTo(123456789L);
//    assertThat(notification.getRecipientUserIds()).containsExactly("recipient");
//    assertThat(notification.getMessage()).isEqualTo("This is a stop notification.");
//    assertThat(notification.getDocument()).isNull();
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createInformNotification")
//  public void receiveInformNotification() throws IOException {
//    boolean success = handleDelivery(ConsumerRoutingKey.INFORM_NOTIFICATION);
//    assertTrue(success);
//
//    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
//    verify(notificationDao).insertNotification(captor.capture());
//
//    Notification notification = captor.getValue();
//    assertThat(notification.getId()).isEqualTo("informNotificationId");
//    assertThat(notification.getCaseReference()).isEqualTo("caseRef");
//    assertThat(notification.getNotificationType()).isEqualByComparingTo(NotificationType.INFORM);
//    assertThat(notification.getCreatedByUserId()).isEqualTo("createdByUserId");
//    assertThat(notification.getCreatedTimestamp()).isEqualTo(123456789L);
//    assertThat(notification.getRecipientUserIds()).containsExactly("recipient");
//    assertThat(notification.getMessage()).isNull();
//    assertThat(notification.getDocument().getId()).isEqualTo("fileId");
//    assertThat(notification.getDocument().getFilename()).isEqualTo("filename");
//    assertThat(notification.getDocument().getUrl()).isEqualTo("url");
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createRfiWithdrawal")
//  public void receiveRfiWithdrawal() throws Exception {
//    boolean success = handleDelivery(ConsumerRoutingKey.RFI_WITHDRAWAL);
//    assertTrue(success);
//
//    ArgumentCaptor<RfiWithdrawal> captor = ArgumentCaptor.forClass(RfiWithdrawal.class);
//    verify(rfiWithdrawalDao).insertRfiWithdrawal(captor.capture());
//
//    RfiWithdrawal rfiWithdrawal = captor.getValue();
//    assertThat(rfiWithdrawal.getId()).isNotEmpty();
//    assertThat(rfiWithdrawal.getRfiId()).isEqualTo("rfiId");
//    assertThat(rfiWithdrawal.getCreatedByUserId()).isEqualTo("createdByUserId");
//    assertThat(rfiWithdrawal.getCreatedTimestamp()).isEqualTo(123456789L);
//    assertThat(rfiWithdrawal.getRecipientUserIds()).containsExactly("recipient");
//    assertThat(rfiWithdrawal.getMessage()).isEqualTo("This is an rfi withdrawal.");
//
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createOutcomeIssueLicence")
//  public void receiveOutcomeIssueLicence() throws Exception {
//    boolean success = handleDelivery(ConsumerRoutingKey.OUTCOME_ISSUE);
//    assertTrue(success);
//
//    ArgumentCaptor<Outcome> captor = ArgumentCaptor.forClass(Outcome.class);
//    verify(outcomeDao).insertOutcome(captor.capture());
//
//    Outcome outcome = captor.getValue();
//    assertThat(outcome.getId()).isEqualTo("outcomeId");
//    assertThat(outcome.getCaseReference()).isEqualTo("caseRef");
//    assertThat(outcome.getCreatedByUserId()).isEqualTo("createdByUserId");
//    assertThat(outcome.getRecipientUserIds()).containsExactly("recipient");
//    assertThat(outcome.getCreatedTimestamp()).isEqualTo(123456789L);
//    assertThat(outcome.getOutcomeDocuments()).hasSize(1);
//    OutcomeDocument outcomeDocument = outcome.getOutcomeDocuments().get(0);
//    assertThat(outcomeDocument.getId()).isEqualTo("documentId");
//    assertThat(outcomeDocument.getDocumentType()).isEqualTo(DocumentType.ISSUE_LICENCE_DOCUMENT);
//    assertThat(outcomeDocument.getLicenceRef()).isEqualTo("licenceRef");
//    assertThat(outcomeDocument.getFilename()).isEqualTo("filename");
//    assertThat(outcomeDocument.getUrl()).isEqualTo("url");
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createOutcomeAmendLicence")
//  public void receiveOutcomeAmendLicence() throws Exception {
//    boolean success = handleDelivery(ConsumerRoutingKey.OUTCOME_AMEND);
//    assertTrue(success);
//
//    ArgumentCaptor<Outcome> captor = ArgumentCaptor.forClass(Outcome.class);
//    verify(outcomeDao).insertOutcome(captor.capture());
//
//    Outcome outcome = captor.getValue();
//    assertThat(outcome.getId()).isEqualTo("outcomeId");
//    assertThat(outcome.getCaseReference()).isEqualTo("caseRef");
//    assertThat(outcome.getCreatedByUserId()).isEqualTo("createdByUserId");
//    assertThat(outcome.getRecipientUserIds()).containsExactly("recipient");
//    assertThat(outcome.getCreatedTimestamp()).isEqualTo(123456789L);
//    assertThat(outcome.getOutcomeDocuments()).hasSize(1);
//    OutcomeDocument outcomeDocument = outcome.getOutcomeDocuments().get(0);
//    assertThat(outcomeDocument.getId()).isEqualTo("documentId");
//    assertThat(outcomeDocument.getDocumentType()).isEqualTo(DocumentType.AMENDMENT_LICENCE_DOCUMENT);
//    assertThat(outcomeDocument.getLicenceRef()).isEqualTo("licenceRef");
//    assertThat(outcomeDocument.getFilename()).isEqualTo("filename");
//    assertThat(outcomeDocument.getUrl()).isEqualTo("url");
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createWithdrawalAcceptance")
//  public void receiveWithdrawalAcceptance() throws Exception {
//    boolean success = handleDelivery(ConsumerRoutingKey.WITHDRAWAL_ACCEPT);
//    assertTrue(success);
//
//    ArgumentCaptor<WithdrawalApproval> captor = ArgumentCaptor.forClass(WithdrawalApproval.class);
//    verify(withdrawalApprovalDao).insertWithdrawalApproval(captor.capture());
//
//    WithdrawalApproval withdrawalApproval = captor.getValue();
//    assertThat(withdrawalApproval.getId()).isEqualTo("withdrawalAcceptId");
//    assertThat(withdrawalApproval.getAppId()).isEqualTo("appId");
//    assertThat(withdrawalApproval.getCreatedByUserId()).isEqualTo("createdByUserId");
//    assertThat(withdrawalApproval.getCreatedTimestamp()).isEqualTo(123456789L);
//    assertThat(withdrawalApproval.getRecipientUserIds()).containsExactly("recipient");
//    assertThat(withdrawalApproval.getMessage()).isEqualTo("This is a withdrawal acceptance.");
//
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createWithdrawalRejection")
//  public void receiveWithdrawalRejection() throws Exception {
//    boolean success = handleDelivery(ConsumerRoutingKey.WITHDRAWAL_REJECTION);
//    assertTrue(success);
//
//    ArgumentCaptor<WithdrawalRejection> captor = ArgumentCaptor.forClass(WithdrawalRejection.class);
//    verify(withdrawalRejectionDao).insertWithdrawalRejection(captor.capture());
//
//    WithdrawalRejection withdrawalRejection = captor.getValue();
//    assertThat(withdrawalRejection.getId()).isNotEmpty();
//    assertThat(withdrawalRejection.getAppId()).isEqualTo("appId");
//    assertThat(withdrawalRejection.getCreatedByUserId()).isEqualTo("createdByUserId");
//    assertThat(withdrawalRejection.getMessage()).isEqualTo("This is a withdrawal rejection.");
//    assertThat(withdrawalRejection.getRecipientUserIds()).containsExactly("recipient");
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createCase")
//  public void receiveCaseCreate() throws Exception {
//    boolean success = handleDelivery(ConsumerRoutingKey.CASE_CREATE);
//    assertTrue(success);
//
//    ArgumentCaptor<CaseDetails> captor = ArgumentCaptor.forClass(CaseDetails.class);
//    verify(caseDetailsDao).insert(captor.capture());
//
//    CaseDetails caseDetails = captor.getValue();
//    assertThat(caseDetails.getAppId()).isEqualTo("appId");
//    assertThat(caseDetails.getCaseReference()).isEqualTo("caseRef");
//    assertThat(caseDetails.getCreatedByUserId()).isEqualTo("createdByUserId");
//    assertThat(caseDetails.getCreatedTimestamp()).isEqualTo(123456789L);
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createCaseOfficerUpdate")
//  public void receiveCaseOfficerUpdate() throws IOException {
//    boolean success = handleDelivery(ConsumerRoutingKey.OFFICER_UPDATE);
//    assertTrue(success);
//
//    verify(applicationDao).updateCaseOfficerId("appId", "caseOfficerId");
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createRfiDeadlineUpdate")
//  public void receiveRfiDeadlineUpdate() throws IOException {
//    boolean success = handleDelivery(ConsumerRoutingKey.RFI_DEADLINE_UPDATE);
//    assertTrue(success);
//
//    verify(rfiDao).updateDeadline("rfiId", 123456789L);
//  }
//
//  private boolean handleDelivery(ConsumerRoutingKey consumerRoutingKey) throws IOException {
//    return messageHandler.handleMessage(consumerRoutingKey.toString(), new String(mockProvider.getMessage()));
//  }
//
}

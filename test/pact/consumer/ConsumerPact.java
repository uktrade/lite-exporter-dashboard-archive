package pact.consumer;

public class ConsumerPact {

//  private final static String PROVIDER = "lite-spire-relay-service";
//  private final static String CONSUMER = "lite-exporter-dashboard";
//
//  @Rule
//  public MessagePactProviderRule mockProvider = new MessagePactProviderRule(PROVIDER, this);
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createStatusUpdate(MessagePactBuilder builder) {
//    PactDslJsonBody body = new PactDslJsonBody();
//    body.stringType(("id"), "id");
//    body.stringValue("appId", "app_id");
//    body.stringValue("statusType", "INITIAL_CHECKS");
//    body.integerType("createdTimestamp", 123456789L);
//
//    return builder.given("initial checks have started")
//        .expectsToReceive("a status update message with status type INITIAL_CHECKS")
//        .withContent(body)
//        .toPact();
//  }
//
//  @Pact(provider = PROVIDER, consumer = CONSUMER)
//  public MessagePact createRfi(MessagePactBuilder builder) {
//    PactDslJsonBody body = new PactDslJsonBody();
//    body.stringType("rfiId", "rfi_id");
//    body.stringType("appId", "app_id");
//    body.stringType("rfiStatus", "ACTIVE");
//    body.stringType("sentBy", "sent_by");
//    body.stringType("message", "This is a rfi message.");
//    body.integerType("receivedTimestamp", 123456789L);
//    body.integerType("dueTimestamp", 1234567890L);
//    return builder.given("a rfi was requested")
//        .expectsToReceive("a rfi message with rfi status ACTIVE")
//        .withContent(body)
//        .toPact();
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createStatusUpdate")
//  public void receiveStatusUpdate() throws Exception {
//    StatusUpdateDao statusUpdateDao = mock(StatusUpdateDao.class);
//    Channel channel = mock(Channel.class);
//    Envelope envelope = mock(Envelope.class);
//    when(envelope.getRoutingKey()).thenReturn(RoutingKey.STATUS_UPDATE.toString());
//    MessageConsumer messageConsumer = new MessageConsumerImpl(null, statusUpdateDao, channel);
//
//    messageConsumer.handleDelivery(null, envelope, null, mockProvider.getMessage());
//
//    StatusUpdate expected = new StatusUpdate("id", "app_id", StatusType.INITIAL_CHECKS, 123456789L);
//    verify(statusUpdateDao).insertStatusUpdate(refEq(expected));
//    verify(channel).basicAck(0, false);
//  }
//
//  @Test
//  @PactVerification(value = PROVIDER, fragment = "createRfi")
//  public void receiveRfi() throws Exception {
//    RfiDao rfiDao = mock(RfiDao.class);
//    Channel channel = mock(Channel.class);
//    Envelope envelope = mock(Envelope.class);
//    when(envelope.getRoutingKey()).thenReturn(RoutingKey.RFI_CREATE.toString());
//    MessageConsumer messageConsumer = new MessageConsumerImpl(rfiDao, null, channel);
//
//    messageConsumer.handleDelivery(null, envelope, null, mockProvider.getMessage());
//
//    Rfi expected = new Rfi("rfi_id", "app_id", RfiStatus.ACTIVE, 123456789L, 1234567890L, "sent_by", "This is a rfi message.");
//    verify(rfiDao).insertRfi(refEq(expected));
//    verify(channel).basicAck(0, false);
//  }

}

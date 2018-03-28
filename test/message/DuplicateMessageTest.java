package message;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static play.inject.Bindings.bind;
import static play.test.Helpers.running;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_5;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import components.message.ConsumerRoutingKey;
import components.message.MessageHandler;
import components.message.SqsPoller;
import components.service.StartUpService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import uk.gov.bis.lite.sielapp.api.SielCreate;
import uk.gov.bis.lite.sielapp.api.SielDelete;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardCaseCreated;
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
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardStatusCode;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardWithdrawalAccept;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.OutcomeDocumentType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class DuplicateMessageTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final String POSTGRES_URL = "localhost";
  private static final boolean POSTGRES_USE_EMBEDDED = true;
  private static EmbeddedPostgres POSTGRES;

  @BeforeClass
  public static void startDatabase() throws IOException {
    if (POSTGRES_USE_EMBEDDED) {
      POSTGRES = new EmbeddedPostgres(V9_5);
      POSTGRES.start(POSTGRES_URL, 5432, "postgres", "postgres", "password");
    }
  }

  @Test
  public void duplicateDashboardRfiCreateShouldByRejected() throws JsonProcessingException {
    Application application = buildApplication();

    running(application, () -> {
      clean(application);

      MessageHandler messageHandler = application.getWrappedApplication().injector().instanceOf(MessageHandler.class);
      insertApplication(messageHandler);

      DashboardRfiCreate dashboardRfiCreate = new DashboardRfiCreate();
      dashboardRfiCreate.setAppId("appId");
      dashboardRfiCreate.setCaseRef("caseRef");
      dashboardRfiCreate.setId("id");
      dashboardRfiCreate.setMessage("Please answer this rfi.");
      dashboardRfiCreate.setCreatedByUserId("createdByUserId");
      dashboardRfiCreate.setCreatedTimestamp(System.currentTimeMillis());
      dashboardRfiCreate.setDeadlineTimestamp(System.currentTimeMillis());
      dashboardRfiCreate.setRecipientUserIds(new ArrayList<>());

      assertTrue(messageHandler.handleMessage(ConsumerRoutingKey.RFI.toString(), toJson(dashboardRfiCreate)));
      assertFalse(messageHandler.handleMessage(ConsumerRoutingKey.RFI.toString(), toJson(dashboardRfiCreate)));
    });
  }

  @Test
  public void duplicateDashboardCaseStatusUpdateShouldBeRejected() throws JsonProcessingException {
    Application application = buildApplication();

    running(application, () -> {
      clean(application);

      MessageHandler messageHandler = application.getWrappedApplication().injector().instanceOf(MessageHandler.class);
      insertApplication(messageHandler);

      DashboardCaseStatusUpdate dashboardCaseStatusUpdate = new DashboardCaseStatusUpdate();
      dashboardCaseStatusUpdate.setAppId("appId");
      dashboardCaseStatusUpdate.setCaseRef("caseRef");
      dashboardCaseStatusUpdate.setCreatedTimestamp(System.currentTimeMillis());
      dashboardCaseStatusUpdate.setStatusCode(DashboardStatusCode.INITIAL_CHECKS);

      assertTrue(messageHandler.handleMessage(ConsumerRoutingKey.STATUS_UPDATE.toString(), toJson(dashboardCaseStatusUpdate)));
      assertFalse(messageHandler.handleMessage(ConsumerRoutingKey.STATUS_UPDATE.toString(), toJson(dashboardCaseStatusUpdate)));
    });
  }

  @Test
  public void duplicateDashboardNotificationDelayShouldBeRejected() {
    Application application = buildApplication();

    running(application, () -> {
      clean(application);

      MessageHandler messageHandler = application.getWrappedApplication().injector().instanceOf(MessageHandler.class);
      insertApplication(messageHandler);

      DashboardNotificationDelay dashboardNotificationDelay = new DashboardNotificationDelay();
      dashboardNotificationDelay.setId("id");
      dashboardNotificationDelay.setAppId("appId");
      dashboardNotificationDelay.setCaseRef("caseRef");
      dashboardNotificationDelay.setRecipientUserIds(new ArrayList<>());
      dashboardNotificationDelay.setCreatedTimestamp(System.currentTimeMillis());
      dashboardNotificationDelay.setMessage("message");

      assertTrue(messageHandler.handleMessage(ConsumerRoutingKey.DELAY_NOTIFICATION.toString(), toJson(dashboardNotificationDelay)));
      assertFalse(messageHandler.handleMessage(ConsumerRoutingKey.DELAY_NOTIFICATION.toString(), toJson(dashboardNotificationDelay)));
    });
  }

  @Test
  public void duplicateDashboardNotificationStopShouldBeRejected() {
    Application application = buildApplication();

    running(application, () -> {
      clean(application);

      MessageHandler messageHandler = application.getWrappedApplication().injector().instanceOf(MessageHandler.class);
      insertApplication(messageHandler);

      DashboardNotificationStop dashboardNotificationStop = new DashboardNotificationStop();
      dashboardNotificationStop.setId("id");
      dashboardNotificationStop.setAppId("appId");
      dashboardNotificationStop.setCaseRef("caseRef");
      dashboardNotificationStop.setCreatedByUserId("createdByUserId");
      dashboardNotificationStop.setRecipientUserIds(new ArrayList<>());
      dashboardNotificationStop.setCreatedTimestamp(System.currentTimeMillis());
      dashboardNotificationStop.setMessage("message");

      assertTrue(messageHandler.handleMessage(ConsumerRoutingKey.STOP_NOTIFICATION.toString(), toJson(dashboardNotificationStop)));
      assertFalse(messageHandler.handleMessage(ConsumerRoutingKey.STOP_NOTIFICATION.toString(), toJson(dashboardNotificationStop)));
    });
  }

  @Test
  public void duplicateDashboardNotificationInformShouldBeRejected() {
    Application application = buildApplication();

    running(application, () -> {
      clean(application);

      MessageHandler messageHandler = application.getWrappedApplication().injector().instanceOf(MessageHandler.class);
      insertApplication(messageHandler);

      DashboardNotificationInform dashboardNotificationInform = new DashboardNotificationInform();
      dashboardNotificationInform.setId("id");
      dashboardNotificationInform.setAppId("appId");
      dashboardNotificationInform.setCaseRef("caseRef");
      dashboardNotificationInform.setCreatedByUserId("createdByUserId");
      dashboardNotificationInform.setRecipientUserIds(new ArrayList<>());
      dashboardNotificationInform.setCreatedTimestamp(System.currentTimeMillis());
      DashboardMessageDocument dashboardMessageDocument = new DashboardMessageDocument();
      dashboardMessageDocument.setFilename("filename");
      dashboardMessageDocument.setId("id");
      dashboardMessageDocument.setUrl("url");
      dashboardNotificationInform.setDocument(dashboardMessageDocument);

      assertTrue(messageHandler.handleMessage(ConsumerRoutingKey.INFORM_NOTIFICATION.toString(), toJson(dashboardNotificationInform)));
      assertFalse(messageHandler.handleMessage(ConsumerRoutingKey.INFORM_NOTIFICATION.toString(), toJson(dashboardNotificationInform)));
    });
  }

  @Test
  public void duplicateDashboardRfiWithdrawalCreateShouldBeRejected() {
    Application application = buildApplication();

    running(application, () -> {
      clean(application);

      MessageHandler messageHandler = application.getWrappedApplication().injector().instanceOf(MessageHandler.class);
      insertApplication(messageHandler);

      DashboardRfiCreate dashboardRfiCreate = new DashboardRfiCreate();
      dashboardRfiCreate.setAppId("appId");
      dashboardRfiCreate.setCaseRef("caseRef");
      dashboardRfiCreate.setId("rfiId");
      dashboardRfiCreate.setMessage("message");
      dashboardRfiCreate.setCreatedByUserId("createdByUserId");
      dashboardRfiCreate.setCreatedTimestamp(System.currentTimeMillis());
      dashboardRfiCreate.setDeadlineTimestamp(System.currentTimeMillis());
      dashboardRfiCreate.setRecipientUserIds(new ArrayList<>());

      messageHandler.handleMessage(ConsumerRoutingKey.RFI.toString(), toJson(dashboardRfiCreate));

      DashboardRfiWithdrawalCreate dashboardRfiWithdrawalCreate = new DashboardRfiWithdrawalCreate();
      dashboardRfiWithdrawalCreate.setRfiId("rfiId");
      dashboardRfiWithdrawalCreate.setAppId("appId");
      dashboardRfiWithdrawalCreate.setCreatedByUserId("createdByUserId");
      dashboardRfiWithdrawalCreate.setMessage("message");
      dashboardRfiWithdrawalCreate.setCreatedTimestamp(System.currentTimeMillis());
      dashboardRfiWithdrawalCreate.setRecipientUserIds(new ArrayList<>());

      assertTrue(messageHandler.handleMessage(ConsumerRoutingKey.RFI_WITHDRAWAL.toString(), toJson(dashboardRfiWithdrawalCreate)));
      assertFalse(messageHandler.handleMessage(ConsumerRoutingKey.RFI_WITHDRAWAL.toString(), toJson(dashboardRfiWithdrawalCreate)));
    });
  }

  @Test
  public void duplicateDashboardOutcomeIssueShouldBeRejected() {
    Application application = buildApplication();

    running(application, () -> {
      clean(application);

      MessageHandler messageHandler = application.getWrappedApplication().injector().instanceOf(MessageHandler.class);
      insertApplication(messageHandler);

      DashboardOutcomeIssue dashboardOutcomeIssue = new DashboardOutcomeIssue();
      dashboardOutcomeIssue.setAppId("appId");
      dashboardOutcomeIssue.setCaseRef("caseRef");
      dashboardOutcomeIssue.setCreatedByUserId("createdByUserId");
      dashboardOutcomeIssue.setId("id");
      dashboardOutcomeIssue.setRecipientUserIds(new ArrayList<>());
      dashboardOutcomeIssue.setCreatedTimestamp(System.currentTimeMillis());
      DashboardOutcomeDocument dashboardOutcomeDocument = new DashboardOutcomeDocument();
      dashboardOutcomeDocument.setId("id");
      dashboardOutcomeDocument.setDocumentType(OutcomeDocumentType.LICENCE_DOCUMENT);
      dashboardOutcomeDocument.setLicenceRef("licenceRef");
      dashboardOutcomeDocument.setFilename("filename");
      dashboardOutcomeDocument.setUrl("url");
      dashboardOutcomeDocument.setLicenceExpiry("licenceExpiry");
      dashboardOutcomeIssue.setDocuments(Collections.singletonList(dashboardOutcomeDocument));

      assertTrue(messageHandler.handleMessage(ConsumerRoutingKey.OUTCOME_ISSUE.toString(), toJson(dashboardOutcomeIssue)));
      assertFalse(messageHandler.handleMessage(ConsumerRoutingKey.OUTCOME_ISSUE.toString(), toJson(dashboardOutcomeDocument)));
    });
  }

  @Test
  public void duplicateDashboardOutcomeAmendShouldBeRejected() {
    Application application = buildApplication();

    running(application, () -> {
      clean(application);

      MessageHandler messageHandler = application.getWrappedApplication().injector().instanceOf(MessageHandler.class);
      insertApplication(messageHandler);

      DashboardOutcomeAmend dashboardOutcomeAmend = new DashboardOutcomeAmend();
      dashboardOutcomeAmend.setAppId("appId");
      dashboardOutcomeAmend.setCaseRef("caseRef");
      dashboardOutcomeAmend.setCreatedByUserId("createdByUserId");
      dashboardOutcomeAmend.setId("id");
      dashboardOutcomeAmend.setRecipientUserIds(new ArrayList<>());
      dashboardOutcomeAmend.setCreatedTimestamp(System.currentTimeMillis());
      DashboardOutcomeDocument dashboardOutcomeDocument = new DashboardOutcomeDocument();
      dashboardOutcomeDocument.setId("id");
      dashboardOutcomeDocument.setDocumentType(OutcomeDocumentType.LICENCE_DOCUMENT);
      dashboardOutcomeDocument.setLicenceRef("licenceRef");
      dashboardOutcomeDocument.setFilename("filename");
      dashboardOutcomeDocument.setUrl("url");
      dashboardOutcomeDocument.setLicenceExpiry("licenceExpiry");
      dashboardOutcomeAmend.setDocuments(Collections.singletonList(dashboardOutcomeDocument));

      assertTrue(messageHandler.handleMessage(ConsumerRoutingKey.OUTCOME_AMEND.toString(), toJson(dashboardOutcomeAmend)));
      assertFalse(messageHandler.handleMessage(ConsumerRoutingKey.OUTCOME_AMEND.toString(), toJson(dashboardOutcomeAmend)));
    });
  }

  @Test
  public void duplicateDashboardWithdrawalAcceptShouldBeRejected() {
    Application application = buildApplication();

    running(application, () -> {
      clean(application);

      MessageHandler messageHandler = application.getWrappedApplication().injector().instanceOf(MessageHandler.class);
      insertApplication(messageHandler);

      DashboardWithdrawalAccept dashboardWithdrawalAccept = new DashboardWithdrawalAccept();
      dashboardWithdrawalAccept.setId("id");
      dashboardWithdrawalAccept.setAppId("appId");
      dashboardWithdrawalAccept.setCaseRef("caseRef");
      dashboardWithdrawalAccept.setRecipientUserIds(new ArrayList<>());
      dashboardWithdrawalAccept.setCreatedTimestamp(System.currentTimeMillis());
      dashboardWithdrawalAccept.setMessage("message");
      dashboardWithdrawalAccept.setCreatedByUserId("createdByUserId");

      assertTrue(messageHandler.handleMessage(ConsumerRoutingKey.WITHDRAWAL_ACCEPT.toString(), toJson(dashboardWithdrawalAccept)));
      assertFalse(messageHandler.handleMessage(ConsumerRoutingKey.WITHDRAWAL_ACCEPT.toString(), toJson(dashboardWithdrawalAccept)));
    });
  }

  @Test
  public void duplicateDashboardCaseCreatedShouldBeRejected() {
    Application application = buildApplication();

    running(application, () -> {
      clean(application);

      MessageHandler messageHandler = application.getWrappedApplication().injector().instanceOf(MessageHandler.class);
      insertSielCreate(messageHandler);

      assertTrue(insertDashboardCaseCreated(messageHandler));
      assertFalse(insertDashboardCaseCreated(messageHandler));
    });
  }

  @Test
  public void duplicateSielCreatedShouldBeRejected() {
    Application application = buildApplication();

    running(application, () -> {
      clean(application);

      MessageHandler messageHandler = application.getWrappedApplication().injector().instanceOf(MessageHandler.class);

      assertTrue(insertSielCreate(messageHandler));
      assertFalse(insertSielCreate(messageHandler));
    });
  }

  @Test
  public void duplicateSielDeleteShouldBeRejected() {
    Application application = buildApplication();

    running(application, () -> {
      clean(application);

      MessageHandler messageHandler = application.getWrappedApplication().injector().instanceOf(MessageHandler.class);
      insertSielCreate(messageHandler);

      SielDelete sielDelete = new SielDelete("appId", "1");

      assertTrue(messageHandler.handleMessage(ConsumerRoutingKey.SIEL_DELETE.toString(), toJson(sielDelete)));
      assertFalse(messageHandler.handleMessage(ConsumerRoutingKey.SIEL_DELETE.toString(), toJson(sielDelete)));
    });
  }

  private Application buildApplication() {
    Config config = ConfigFactory.load("conf/test-application.conf");
    return new GuiceApplicationBuilder()
        .configure(config)
        .overrides(bind(SqsPoller.class).toInstance(mock(SqsPoller.class)))
        .overrides(bind(StartUpService.class).to(TestStartUpServiceImpl.class).eagerly())
        .configure("db.default.url", "jdbc:postgresql://" + POSTGRES_URL + ":5432/postgres?currentSchema=test")
        .build();
  }

  private void clean(Application application) {
    DBI dbi = application.getWrappedApplication().injector().instanceOf(DBI.class);
    try (Handle handle = dbi.open()) {
      handle.execute("TRUNCATE TABLE APPLICATION, CASE_DETAILS, STATUS_UPDATE, RFI, RFI_REPLY, RFI_WITHDRAWAL, AMENDMENT, WITHDRAWAL_REQUEST, WITHDRAWAL_REJECTION, WITHDRAWAL_APPROVAL, DRAFT_FILE, OUTCOME, NOTIFICATION, READ, BACKLOG");
    }
  }

  private void insertApplication(MessageHandler messageHandler) {
    insertSielCreate(messageHandler);
    insertDashboardCaseCreated(messageHandler);
  }

  private boolean insertDashboardCaseCreated(MessageHandler messageHandler) {
    DashboardCaseCreated dashboardCaseCreated = new DashboardCaseCreated();
    dashboardCaseCreated.setAppId("appId");
    dashboardCaseCreated.setCaseRef("caseRef");
    dashboardCaseCreated.setCreatedByUserId("createdByUserId");
    dashboardCaseCreated.setCreatedTimestamp(System.currentTimeMillis());
    return messageHandler.handleMessage(ConsumerRoutingKey.CASE_CREATE.toString(), toJson(dashboardCaseCreated));
  }

  private boolean insertSielCreate(MessageHandler messageHandler) {
    SielCreate sielCreate = new SielCreate();
    sielCreate.setAppId("appId");
    sielCreate.setCreatedByUserId("createdByUserId");
    sielCreate.setCreatedTimestamp(System.currentTimeMillis());
    return messageHandler.handleMessage(ConsumerRoutingKey.SIEL_CREATE.toString(), toJson(sielCreate));
  }

  @AfterClass
  public static void stopDatabase() {
    if (POSTGRES_USE_EMBEDDED) {
      POSTGRES.stop();
    }
  }

  private String toJson(Object object) {
    try {
      return MAPPER.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}

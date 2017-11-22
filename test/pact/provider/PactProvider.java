package pact.provider;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.AmqpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.runner.RunWith;
import uk.gov.bis.lite.exporterdashboard.api.AmendmentMessage;
import uk.gov.bis.lite.exporterdashboard.api.DashboardDocument;
import uk.gov.bis.lite.exporterdashboard.api.NotificationReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.OutcomeReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.RfiReplyMessage;
import uk.gov.bis.lite.exporterdashboard.api.RfiWithdrawalReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequestAcceptReadMessage;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequestMessage;

@RunWith(PactRunner.class)
@Provider("lite-exporter-dashboard")
@PactBroker(host = "pact-broker.mgmt.licensing.service.trade.gov.uk.test", port = "80")
public class PactProvider {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @TestTarget
  public final Target target = new AmqpTarget(Collections.singletonList("pact.provider.*"));

  @PactVerifyProvider("an rfi was replied to")
  public String verifyRfiReplyMessage() throws JsonProcessingException {
    RfiReplyMessage rfiReplyMessage = new RfiReplyMessage();
    rfiReplyMessage.setId("rfiReplyId");
    rfiReplyMessage.setRfiId("rfiId");
    rfiReplyMessage.setAppId("appId");
    rfiReplyMessage.setCreatedByUserId("createdByUserId");
    rfiReplyMessage.setCreatedTimestamp(123456789L);
    rfiReplyMessage.setMessage("This is an rfi reply message.");
    rfiReplyMessage.setAttachments(createDashboardDocument());
    return MAPPER.writeValueAsString(rfiReplyMessage);
  }

  @PactVerifyProvider("a withdrawal was requested")
  public String verifyWithdrawalRequestMessage() throws JsonProcessingException {
    WithdrawalRequestMessage withdrawalRequestMessage = new WithdrawalRequestMessage();
    withdrawalRequestMessage.setId("withdrawalRequestId");
    withdrawalRequestMessage.setAppId("appId");
    withdrawalRequestMessage.setCreatedByUserId("createdByUserId");
    withdrawalRequestMessage.setCreatedTimestamp(123456789L);
    withdrawalRequestMessage.setMessage("This is a withdrawal request.");
    withdrawalRequestMessage.setAttachments(createDashboardDocument());
    return MAPPER.writeValueAsString(withdrawalRequestMessage);
  }

  @PactVerifyProvider("an application was amended")
  public String verifyAmendmentMessage() throws JsonProcessingException {
    AmendmentMessage amendmentMessage = new AmendmentMessage();
    amendmentMessage.setId("amendmentId");
    amendmentMessage.setAppId("appId");
    amendmentMessage.setCreatedByUserId("createdByUserId");
    amendmentMessage.setCreatedTimestamp(123456789L);
    amendmentMessage.setMessage("This is an amendment.");
    amendmentMessage.setAttachments(createDashboardDocument());
    return MAPPER.writeValueAsString(amendmentMessage);
  }

  @PactVerifyProvider("an rfi withdrawal was read")
  public String verifyRfiWithdrawalReadMessage() throws JsonProcessingException {
    RfiWithdrawalReadMessage rfiWithdrawalReadMessage = new RfiWithdrawalReadMessage();
    rfiWithdrawalReadMessage.setAppId("appId");
    rfiWithdrawalReadMessage.setRfiId("rfiId");
    rfiWithdrawalReadMessage.setCreatedByUserId("createdByUserId");
    return MAPPER.writeValueAsString(rfiWithdrawalReadMessage);
  }

  @PactVerifyProvider("an outcome was read")
  public String verifyOutcomeReadMessage() throws JsonProcessingException {
    OutcomeReadMessage outcomeReadMessage = new OutcomeReadMessage();
    outcomeReadMessage.setOutcomeId("outcomeId");
    outcomeReadMessage.setAppId("appId");
    outcomeReadMessage.setCreatedByUserId("createdByUserId");
    return MAPPER.writeValueAsString(outcomeReadMessage);
  }

  @PactVerifyProvider("a notification was read")
  public String verifyInformNotificationReadMessage() throws JsonProcessingException {
    NotificationReadMessage notificationReadMessage = new NotificationReadMessage();
    notificationReadMessage.setNotificationId("notificationId");
    notificationReadMessage.setCreatedByUserId("createdByUserId");
    notificationReadMessage.setAppId("appId");
    return MAPPER.writeValueAsString(notificationReadMessage);
  }

  @PactVerifyProvider("a withdrawal request accept was read")
  public String verifyWithdrawalRequestAcceptReadMessage() throws JsonProcessingException {
    WithdrawalRequestAcceptReadMessage withdrawalRequestAcceptReadMessage = new WithdrawalRequestAcceptReadMessage();
    withdrawalRequestAcceptReadMessage.setNotificationId("withdrawalRequestAcceptNotificationId");
    withdrawalRequestAcceptReadMessage.setCreatedByUserId("createdByUserId");
    withdrawalRequestAcceptReadMessage.setAppId("appId");
    return MAPPER.writeValueAsString(withdrawalRequestAcceptReadMessage);
  }

  private List<DashboardDocument> createDashboardDocument() {
    DashboardDocument dashboardDocument = new DashboardDocument();
    dashboardDocument.setId("fileId");
    dashboardDocument.setFilename("filename.pdf");
    dashboardDocument.setUrl("http://www.test.com");
    return Collections.singletonList(dashboardDocument);
  }

}

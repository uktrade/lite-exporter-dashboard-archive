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
import models.Amendment;
import models.RfiResponse;
import models.WithdrawalRequest;
import org.junit.runner.RunWith;

@RunWith(PactRunner.class)
@Provider("lite-exporter-dashboard")
@PactBroker(host = "pact-broker.mgmt.licensing.service.trade.gov.uk.test", port = "80")
public class PactProvider {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @TestTarget
  public final Target target = new AmqpTarget();

  @PactVerifyProvider("a rfi was replied to")
  public String verifyRfiResponse() throws JsonProcessingException {
    RfiResponse rfiResponse = new RfiResponse("rfiId", "sentBy", 123456789L, "This is a rfi reply message.", null);
    return MAPPER.writeValueAsString(rfiResponse);
  }

  @PactVerifyProvider("a withdrawal was requested")
  public String verifyWithdrawalRequest() throws JsonProcessingException {
    WithdrawalRequest withdrawalRequest = new WithdrawalRequest("withdrawalRequestId",
        "appId",
        123456789L,
        "sentBy",
        "This is a withdrawal request message.",
        null,
        null,
        null,
        null);
    return MAPPER.writeValueAsString(withdrawalRequest);
  }

  @PactVerifyProvider("an application was amended")
  public String verifyAmendment() throws JsonProcessingException {
    Amendment amendment = new Amendment("amendmentId", "appId", 123456789L, "sentBy", "This is an amendment message", null);
    return MAPPER.writeValueAsString(amendment);
  }

}

package components.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.common.logging.CorrelationId;
import components.common.logging.ServiceClientLogger;
import components.exceptions.ServiceException;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class OgelServiceClientImpl implements OgelServiceClient {

  private final HttpExecutionContext httpExecutionContext;
  private final WSClient wsClient;
  private final int timeout;
  private final String address;
  private final String credentials;

  @Inject
  public OgelServiceClientImpl(HttpExecutionContext httpExecutionContext,
                               WSClient wsClient,
                               @Named("ogelServiceAddress") String address,
                               @Named("ogelServiceTimeout") int timeout,
                               @Named("ogelServiceCredentials") String credentials) {
    this.httpExecutionContext = httpExecutionContext;
    this.wsClient = wsClient;
    this.address = address;
    this.timeout = timeout;
    this.credentials = credentials;
  }

  @Override
  public OgelFullView getOgel(String ogelId) {
    String url = String.format("%s/ogels/%s", address, ogelId);
    WSRequest req = wsClient.url(url)
        .setAuth(credentials)
        .withRequestFilter(CorrelationId.requestFilter)
        .withRequestFilter(ServiceClientLogger.requestFilter("Ogel Data", "GET", httpExecutionContext))
        .setRequestTimeout(timeout);
    CompletionStage<OgelFullView> request = req.get().handle((response, error) -> {
      if (error != null) {
        String message = String.format("Unable to get ogel with id %s", ogelId);
        throw new ServiceException(message, error);
      } else if (response.getStatus() != 200) {
        String message = String.format("Unexpected HTTP status code %d. Unable to get ogel with id %s", response.getStatus(), ogelId);
        throw new ServiceException(message);
      } else {
        return Json.fromJson(response.asJson(), OgelFullView.class);
      }
    });
    try {
      return request.toCompletableFuture().get();
    } catch (InterruptedException | ExecutionException error) {
      String message = String.format("Unable to get ogel with id %s", ogelId);
      throw new ServiceException(message, error);
    }
  }

}

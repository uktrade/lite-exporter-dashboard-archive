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
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class CustomerServiceClientImpl implements CustomerServiceClient {

  private final HttpExecutionContext httpExecutionContext;
  private final WSClient wsClient;
  private final int timeout;
  private final String address;

  @Inject
  public CustomerServiceClientImpl(HttpExecutionContext httpExecutionContext,
                                   WSClient wsClient,
                                   @Named("customerServiceAddress") String address,
                                   @Named("customerServiceTimeout") int timeout) {
    this.httpExecutionContext = httpExecutionContext;
    this.wsClient = wsClient;
    this.address = address;
    this.timeout = timeout;
  }

  @Override
  public CustomerView getCustomer(String customerId) {
    String url = String.format("%s/customers/%s", address, customerId);
    WSRequest req = wsClient.url(url)
        .withRequestFilter(CorrelationId.requestFilter)
        .withRequestFilter(ServiceClientLogger.requestFilter("Customer", "GET", httpExecutionContext))
        .setRequestTimeout(timeout);
    CompletionStage<CustomerView> request = req.get().handle((response, error) -> {
      if (error != null) {
        String message = String.format("Unable to get customer with id %s", customerId);
        throw new ServiceException(message, error);
      } else if (response.getStatus() != 200) {
        String message = String.format("Unexpected HTTP status code %d. Unable to getCustomer customer with id %s", response.getStatus(), customerId);
        throw new ServiceException(message);
      } else {
        return Json.fromJson(response.asJson(), CustomerView.class);
      }
    });
    try {
      return request.toCompletableFuture().get();
    } catch (InterruptedException | ExecutionException error) {
      String message = String.format("Unable to get customer with id %s", customerId);
      throw new ServiceException(message, error);
    }
  }

  @Override
  public SiteView getSite(String siteId) {
    String url = String.format("%s/sites/%s", address, siteId);
    WSRequest req = wsClient.url(url)
        .withRequestFilter(CorrelationId.requestFilter)
        .withRequestFilter(ServiceClientLogger.requestFilter("Site", "GET", httpExecutionContext))
        .setRequestTimeout(timeout);
    CompletionStage<SiteView> request = req.get().handle((response, error) -> {
      if (error != null) {
        String message = String.format("Unable to get site with id %s", siteId);
        throw new ServiceException(message, error);
      } else if (response.getStatus() != 200) {
        String message = String.format("Unexpected HTTP status code %d. Unable to get site with id %s", response.getStatus(), siteId);
        throw new ServiceException(message);
      } else {
        return Json.fromJson(response.asJson(), SiteView.class);
      }
    });
    try {
      return request.toCompletableFuture().get();
    } catch (InterruptedException | ExecutionException error) {
      String message = String.format("Unable to site with id %s", siteId);
      throw new ServiceException(message, error);
    }
  }

}

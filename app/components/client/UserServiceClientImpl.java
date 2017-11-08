package components.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.common.logging.CorrelationId;
import components.exceptions.ServiceException;
import filters.common.JwtRequestFilter;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import play.libs.Json;
import play.libs.ws.WSClient;
import uk.gov.bis.lite.user.api.view.UserPrivilegesView;

public class UserServiceClientImpl implements UserServiceClient {

  private static final String USER_PRIVILEGES_PATH = "/user-privileges/";

  private final JwtRequestFilter jwtRequestFilter;
  private final WSClient wsClient;
  private final String address;
  private final int timeout;

  @Inject
  public UserServiceClientImpl(WSClient wsClient,
                               JwtRequestFilter jwtRequestFilter,
                               @Named("userServiceAddress") String address,
                               @Named("userServiceTimeout") int timeout) {
    this.wsClient = wsClient;
    this.jwtRequestFilter = jwtRequestFilter;
    this.address = address;
    this.timeout = timeout;
  }

  @Override
  public Optional<UserPrivilegesView> getUserPrivilegeView(String userId) {
    String url = address + USER_PRIVILEGES_PATH + userId;
    CompletableFuture<UserPrivilegesView> request = wsClient.url(url)
        .withRequestFilter(CorrelationId.requestFilter)
        .withRequestFilter(jwtRequestFilter)
        .setRequestTimeout(timeout)
        .get()
        .handle((response, error) -> {
          if (error != null) {
            String message = "Unable to get user privileges view with id " + userId;
            throw new ServiceException(message, error);
          } else if (response.getStatus() != 200) {
            String message = String.format("Unexpected HTTP status code %d. Unable to get user privileges view with id %s", response.getStatus(), userId);
            throw new ServiceException(message);
          } else {
            return Json.fromJson(response.asJson(), UserPrivilegesView.class);
          }
        })
        .toCompletableFuture();
    try {
      return Optional.of(request.get());
    } catch (InterruptedException | ExecutionException error) {
      String message = String.format("Unable to get user privileges view with id %s", userId);
      throw new ServiceException(message, error);
    }
  }

}

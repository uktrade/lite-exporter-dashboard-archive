package components.client;

import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.common.logging.CorrelationId;
import components.common.logging.ServiceClientLogger;
import components.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Http.MultipartFormData.DataPart;
import play.mvc.Http.MultipartFormData.FilePart;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;

public class VirusCheckerClientImpl implements VirusCheckerClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(VirusCheckerClientImpl.class);

  private final HttpExecutionContext httpExecutionContext;
  private final WSClient wsClient;
  private final String credentials;
  private final String address;
  private final int timeout;

  @Inject
  public VirusCheckerClientImpl(HttpExecutionContext httpExecutionContext,
                                WSClient wsClient,
                                @Named("virusServiceCredentials") String credentials,
                                @Named("virusServiceAddress") String address,
                                @Named("virusServiceTimeout") int timeout) {
    this.httpExecutionContext = httpExecutionContext;
    this.wsClient = wsClient;
    this.credentials = credentials;
    this.address = address;
    this.timeout = timeout;
  }

  @Override
  public CompletionStage<Boolean> isOk(Path path) {
    WSRequest req = wsClient.url(address)
        .withRequestFilter(CorrelationId.requestFilter)
        .withRequestFilter(ServiceClientLogger.requestFilter("VirusCheck", "POST", httpExecutionContext))
        .setAuth(credentials)
        .setRequestTimeout(timeout);
    // https://www.playframework.com/documentation/2.5.x/JavaWS#Submitting-multipart/form-data
    Source<ByteString, ?> file = FileIO.fromFile(path.toFile());
    FilePart<Source<ByteString, ?>> fp = new FilePart<>("file", "file.txt", "text/plain", file);
    DataPart dp = new DataPart("key", "value");
    CompletionStage<String> request = req.post(Source.from(Arrays.asList(fp, dp))).handle((response, error) -> {
      if (error != null) {
        String message = "Unable to virus check file " + path.toString();
        throw new ServiceException(message, error);
      } else if (response.getStatus() != 200) {
        String message = String.format("Unexpected HTTP status code %d. Unable to virus check file  %s",
            response.getStatus(), path.toString());
        throw new ServiceException(message);
      } else {
        return response.getBody();
      }
    });
    return request.toCompletableFuture().thenApply("OK"::equals);
  }

}

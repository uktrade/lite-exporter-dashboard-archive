package components.exceptions;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.notFound;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import controllers.common.ErrorHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class DashboardErrorHandler extends ErrorHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(DashboardErrorHandler.class);

  private final views.html.notFound notFound;

  @Inject
  public DashboardErrorHandler(Environment environment, OptionalSourceMapper sourceMapper, Config config,
                               views.html.notFound notFound, @Named("ecjuEmailAddress") String errorContactEmail) {
    super(environment, sourceMapper, config, errorContactEmail);
    this.notFound = notFound;
  }

  @Override
  public CompletionStage<Result> onClientError(Http.RequestHeader request, int statusCode, String message) {
    if (statusCode == Http.Status.NOT_FOUND || statusCode == Http.Status.BAD_REQUEST) {
      LOGGER.warn(statusCode + " " + message);
      return CompletableFuture.completedFuture(notFound(notFound.render()));
    } else {
      return super.onClientError(request, statusCode, message);
    }
  }

  @Override
  public CompletionStage<Result> onServerError(Http.RequestHeader request, Throwable exception) {
    if (ExceptionUtils.indexOfThrowable(exception, UnknownParameterException.class) != -1) {
      return CompletableFuture.completedFuture(badRequest(notFound.render()));
    } else {
      return super.onServerError(request, exception);
    }
  }

}

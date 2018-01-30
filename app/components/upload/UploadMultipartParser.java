package components.upload;

import static play.mvc.Results.badRequest;

import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.StreamConverters;
import akka.util.ByteString;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.http.HttpConfiguration;
import play.core.parsers.Multipart;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import scala.Option;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class UploadMultipartParser extends BodyParser.DelegatingMultipartFormDataBodyParser<TempFile> {

  private static final List<String> FORBIDDEN_FILE_EXTENSIONS = Arrays.asList("exe", "bat", "cmd");
  private static final Logger LOGGER = LoggerFactory.getLogger(UploadMultipartParser.class);
  private static final String CONTENT_LENGTH_DEFAULT = "999999999";
  private static final long CONTENT_LENGTH_MAX = 10 * 1024 * 1024;

  @Inject
  public UploadMultipartParser(Materializer materializer, HttpConfiguration httpConfig) {
    super(materializer, httpConfig.parser().maxDiskBuffer());
  }

  @Override
  public Accumulator<ByteString, F.Either<Result, Http.MultipartFormData<TempFile>>> apply(Http.RequestHeader request) {
    String header = request.getHeader("Content-Length");
    if (header == null) {
      header = CONTENT_LENGTH_DEFAULT;
      LOGGER.warn("Content-Length not set, defaulting to {}", CONTENT_LENGTH_DEFAULT);
    }
    long contentLength;
    try {
      contentLength = Long.parseLong(header);
    } catch (NumberFormatException nfe) {
      LOGGER.warn("Rejecting request of size " + header);
      return Accumulator.done(F.Either.Left(badRequest("bad content length")));
    }
    if (contentLength < 1) {
      LOGGER.warn("Rejecting request of size " + header);
      return Accumulator.done(F.Either.Left(badRequest("bad content length")));
    } else if (contentLength > CONTENT_LENGTH_MAX) {
      LOGGER.warn("Rejecting request of size " + header);
      return Accumulator.done(F.Either.Left(badRequest("content length too big")));
    } else {
      return super.apply(request);
    }
  }

  /**
   * Creates a file part handler that uses a custom accumulator.
   */
  @Override
  public Function<Multipart.FileInfo, Accumulator<ByteString, Http.MultipartFormData.FilePart<TempFile>>> createFilePartHandler() {
    return (Multipart.FileInfo fileInfo) -> {
      String filename = fileInfo.fileName();
      String partName = fileInfo.partName();
      String contentType = parse(fileInfo.contentType());
      // Workaround for bug https://github.com/playframework/playframework/issues/6203, i.e.
      // even if the user doesn't select any file, this code is called with filename as empty string
      if ("".equals(filename)) {
        return Accumulator.done(new Http.MultipartFormData.FilePart<>(partName, filename, contentType, null));
      } else if (StringUtils.isBlank(filename)) {
        String errorMessage = "Blank filename not allowed";
        TempFile tempFile = new TempFile(filename, null, errorMessage);
        return Accumulator.done(new Http.MultipartFormData.FilePart<>(partName, filename, contentType, tempFile));
      } else {
        Optional<String> forbiddenFileEnding = getForbiddenFileEnding(filename);
        if (forbiddenFileEnding.isPresent()) {
          String errorMessage = "File ending not allowed: " + forbiddenFileEnding.get();
          TempFile tempFile = new TempFile(filename, null, errorMessage);
          return Accumulator.done(new Http.MultipartFormData.FilePart<>(partName, filename, contentType, tempFile));
        } else {
          Path path;
          try {
            path = Files.createTempFile("lite-exporter-dashboard", null);
          } catch (IOException ioe) {
            throw new RuntimeException("Unable to create temp file", ioe);
          }
          TempFile tempFile = new TempFile(filename, path, null);
          Sink<ByteString, CompletionStage<IOResult>> sink = StreamConverters.fromOutputStream(() -> new FileOutputStream(path.toFile()));
          return Accumulator.fromSink(
              sink.mapMaterializedValue(completionStage ->
                  completionStage.thenApplyAsync(results ->
                      new Http.MultipartFormData.FilePart<>(partName, filename, contentType, tempFile))
              ));
        }
      }
    };
  }

  private Optional<String> getForbiddenFileEnding(String filename) {
    String lowercase = filename.toLowerCase();
    return FORBIDDEN_FILE_EXTENSIONS.stream()
        .filter(lowercase::endsWith)
        .findAny();
  }

  private String parse(Option<String> stringOption) {
    return stringOption.isDefined() ? stringOption.get() : null;
  }

}
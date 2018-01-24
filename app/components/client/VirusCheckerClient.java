package components.client;

import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

public interface VirusCheckerClient {

  CompletionStage<Boolean> isOk(Path path);

}

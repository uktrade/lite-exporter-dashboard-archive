package components.upload;

import java.nio.file.Path;

public class TempFile {

  private final String filename;
  private final Path path;
  private final String error;

  public TempFile(String filename, Path path, String error) {
    this.filename = filename;
    this.path = path;
    this.error = error;
  }

  public boolean isValid() {
    return getError() == null;
  }

  public String getFilename() {
    return filename;
  }

  public Path getPath() {
    return path;
  }

  public String getError() {
    return error;
  }

}

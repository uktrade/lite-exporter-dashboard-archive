package components.upload;

public class UploadFile {

  private final String originalFilename;
  private final String destinationPath;
  private final String processErrorInfo;

  public UploadFile(String originalFilename, String destinationPath, String processErrorInfo) {
    this.originalFilename = originalFilename;
    this.destinationPath = destinationPath;
    this.processErrorInfo = processErrorInfo;
  }

  public String getOriginalFilename() {
    return originalFilename;
  }

  public String getDestinationPath() {
    return destinationPath;
  }

  public String getProcessErrorInfo() {
    return processErrorInfo;
  }

}

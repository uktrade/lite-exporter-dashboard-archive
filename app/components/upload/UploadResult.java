package components.upload;

public class UploadResult {

  private final String id;
  private final String filename;
  private final String bucket;
  private final String folder;
  private final Long size;
  private final String error;

  public UploadResult(String id, String filename, String bucket, String folder, Long size, String error) {
    this.id = id;
    this.bucket = bucket;
    this.folder = folder;
    this.filename = filename;
    this.size = size;
    this.error = error;
  }

  public String getId() {
    return id;
  }

  public String getFilename() {
    return filename;
  }

  public String getBucket() {
    return bucket;
  }

  public String getFolder() {
    return folder;
  }

  public Long getSize() {
    return size;
  }

  public String getError() {
    return error;
  }

  public boolean isValid() {
    return getError() == null;
  }

  public static UploadResult successfulUpload(String id, String filename, String bucket, String folder, Long size, String error) {
    return new UploadResult(id, filename, bucket, folder, size, error);
  }

  public static UploadResult failedUpload(String filename, String error) {
    return new UploadResult(null, filename, null, null, null, error);
  }

}

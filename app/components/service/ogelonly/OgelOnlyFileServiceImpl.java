package components.service.ogelonly;

import components.common.upload.FileService;
import components.common.upload.UploadResult;
import play.mvc.Http;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class OgelOnlyFileServiceImpl implements FileService {

  @Override
  public CompletionStage<List<UploadResult>> processUpload(String folder, Http.Request request) {
    return null;
  }

  @Override
  public InputStream retrieveFile(String id, String bucket, String folder) {
    return null;
  }

  @Override
  public void deleteFile(String id, String bucket, String folder) {
    // do nothing
  }

  @Override
  public UploadResult uploadToS3(String folder, String filename, Path path) {
    return null;
  }

}

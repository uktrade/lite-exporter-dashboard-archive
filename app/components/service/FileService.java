package components.service;

import components.upload.UploadResult;
import models.enums.DraftType;
import play.mvc.Http;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletionStage;

public interface FileService {

  CompletionStage<List<UploadResult>> processUpload(String appId, Http.Request request);

  void deleteDraftFile(String fileId, String relatedId, DraftType draftType);

  InputStream retrieveFile(String id, String bucket, String folder);

}

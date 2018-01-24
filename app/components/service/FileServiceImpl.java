package components.service;

import static components.util.RandomIdUtil.fileId;
import static java.util.concurrent.CompletableFuture.completedFuture;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.spotify.futures.CompletableFutures;
import components.client.VirusCheckerClient;
import components.dao.DraftFileDao;
import components.upload.TempFile;
import components.upload.UploadResult;
import models.Attachment;
import models.enums.DraftType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class FileServiceImpl implements FileService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

  private final VirusCheckerClient virusCheckerClient;
  private final AmazonS3 amazonS3;
  private final String awsBucketName;
  private final DraftFileDao draftFileDao;
  private final HttpExecutionContext context;

  @Inject
  public FileServiceImpl(VirusCheckerClient virusCheckerClient,
                         AmazonS3 amazonS3,
                         @Named("awsBucketName") String awsBucketName,
                         DraftFileDao draftFileDao,
                         HttpExecutionContext httpExecutionContext) {
    this.virusCheckerClient = virusCheckerClient;
    this.amazonS3 = amazonS3;
    this.awsBucketName = awsBucketName;
    this.draftFileDao = draftFileDao;
    this.context = httpExecutionContext;
  }

  @Override
  public CompletionStage<List<UploadResult>> processUpload(String folder, Http.Request request) {
    return getTempFilesFromRequest(request).stream()
        .map(tempFile -> processUpload(folder, tempFile))
        .collect(CompletableFutures.joinList());
  }

  private CompletionStage<UploadResult> processUpload(String folder, TempFile tempFile) {
    if (tempFile.isValid()) {
      return checkForVirus(tempFile).thenApplyAsync(file -> uploadToS3AndDeleteTempFile(folder, file), context.current());
    } else {
      return completedFuture(UploadResult.failedUpload(tempFile.getFilename(), tempFile.getError()));
    }
  }

  private UploadResult uploadToS3AndDeleteTempFile(String folder, TempFile tempFile) {
    try {
      if (tempFile.isValid()) {
        String id = fileId();
        File file = tempFile.getPath().toFile();
        try {
          amazonS3.putObject(new PutObjectRequest(awsBucketName, folder + "/" + id, file));
        } catch (Exception exception) {
          LOGGER.error("Unable to upload file with filename {} and path {} to amazon s3", tempFile.getFilename(), tempFile.getPath());
          return UploadResult.failedUpload(tempFile.getFilename(), "An unexpected error occurred.");
        }
        long size = file.length();
        return UploadResult.successfulUpload(id, tempFile.getFilename(), awsBucketName, folder, size, null);
      } else {
        return UploadResult.failedUpload(tempFile.getFilename(), tempFile.getError());
      }
    } finally {
      deleteTempFile(tempFile);
    }
  }

  @Override
  public void deleteDraftFile(String fileId, String relatedId, DraftType draftType) {
    Optional<Attachment> attachmentOptional = draftFileDao.getAttachments(relatedId, draftType).stream()
        .filter(draftFile -> draftFile.getId().equals(fileId))
        .findAny();
    if (attachmentOptional.isPresent()) {
      try {
        Attachment attachment = attachmentOptional.get();
        amazonS3.deleteObject(attachment.getBucket(), attachment.getFolder() + "/" + attachment.getId());
      } catch (Exception exception) {
        String message = String.format("Unable to delete file with fileId %s and relatedId %s and draftType %s", fileId, relatedId, draftType);
        throw new RuntimeException(message, exception);
      }
      draftFileDao.deleteDraftFile(fileId, relatedId, draftType);
    } else {
      String message = String.format("Unable to delete file with fileId %s and relatedId %s and draftType %s", fileId, relatedId, draftType);
      throw new RuntimeException(message);
    }
  }

  private void deleteTempFile(TempFile tempFile) {
    if (tempFile.getPath() != null) {
      tempFile.getPath().toFile().delete();
    }
  }

  @Override
  public InputStream retrieveFile(String id, String bucket, String folder) {
    return amazonS3.getObject(bucket, folder + "/" + id).getObjectContent();
  }

  private CompletionStage<TempFile> checkForVirus(TempFile tempFile) {
    if (tempFile.isValid()) {
      return virusCheckerClient.isOk(tempFile.getPath())
          .thenApply(isOk -> {
            if (isOk) {
              return tempFile;
            } else {
              LOGGER.error("File with filename {} and path {} did not pass virus check", tempFile.getFilename(), tempFile.getPath());
              return new TempFile(tempFile.getFilename(), tempFile.getPath(), "Invalid file");
            }
          })
          .exceptionally(error -> {
            LOGGER.error("A network exception occurred while virus checking file with filename {} and path {}",
                tempFile.getFilename(), tempFile.getPath());
            return new TempFile(tempFile.getFilename(), tempFile.getPath(), "An unexpected error occurred.");
          });
    } else {
      return CompletableFuture.completedFuture(tempFile);
    }
  }

  /**
   * Null check on 'uploadFile' is workaround for bug https://github.com/playframework/playframework/issues/6203
   */
  private List<TempFile> getTempFilesFromRequest(Http.Request request) {
    Http.MultipartFormData<TempFile> body = request.body().asMultipartFormData();
    return body.getFiles().stream()
        .map(Http.MultipartFormData.FilePart::getFile)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

}

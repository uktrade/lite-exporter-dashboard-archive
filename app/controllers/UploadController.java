package controllers;

import static components.util.RandomIdUtil.fileId;
import static java.util.concurrent.CompletableFuture.completedFuture;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.exceptions.DatabaseException;
import components.service.AppDataService;
import components.service.UserPrivilegeService;
import components.service.UserService;
import components.upload.UploadFile;
import components.upload.UploadMultipartParser;
import components.util.EnumUtil;
import components.util.FileUtil;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import models.AppData;
import models.File;
import models.FileUploadResponse;
import models.FileUploadResponseItem;
import models.enums.DraftType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.With;

@With(AppGuardAction.class)
public class UploadController extends SamlController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

  private final AppDataService appDataService;
  private final UserService userService;
  private final DraftDao draftDao;
  private final UserPrivilegeService userPrivilegeService;

  @Inject
  public UploadController(AppDataService appDataService, UserService userService, DraftDao draftDao, UserPrivilegeService userPrivilegeService) {
    this.appDataService = appDataService;
    this.userService = userService;
    this.draftDao = draftDao;
    this.userPrivilegeService = userPrivilegeService;
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public CompletionStage<Result> submitFiles(String appId, String draftTypeStr, String relatedId) {
    String userId = userService.getCurrentUserId();
    DraftType draftType = EnumUtil.parse(draftTypeStr, DraftType.class, null);
    if (draftType == null) {
      return completedFuture(badRequest("Unknown draftType " + draftTypeStr));
    } else if (!hasPermissions(userId, appId, draftType, relatedId)) {
      return completedFuture(notFound("Unknown relatedId " + relatedId));
    } else {
      List<UploadFile> files = FileUtil.getUploadFiles(request());
      FileUploadResponse fileUploadResponse = insertDraftAttachments(appId, relatedId, files, draftType);
      return completedFuture(ok(Json.toJson(fileUploadResponse)));
    }
  }

  private boolean hasPermissions(String userId, String appId, DraftType draftType, String relatedId) {
    AppData appData = appDataService.getAppData(appId);
    if (draftType == DraftType.AMENDMENT_OR_WITHDRAWAL) {
      return appId.equals(relatedId) && userPrivilegeService.isAmendmentOrWithdrawalAllowed(userId, appData);
    } else if (draftType == DraftType.RFI_REPLY) {
      return userPrivilegeService.isReplyAllowed(userId, relatedId, appData);
    } else {
      return false;
    }
  }

  public CompletionStage<Result> deleteFile(String appId) {
    String userId = userService.getCurrentUserId();
    Map<String, String[]> formFields = request().body().asFormUrlEncoded();
    String relatedId = formFields.get("relatedId")[0];
    String fileType = formFields.get("fileType")[0];
    String fileId = formFields.get("fileId")[0];
    DraftType draftType = EnumUtil.parse(fileType, DraftType.class, null);
    if (draftType == null) {
      return completedFuture(badRequest("Unknown draftType " + fileType));
    } else if (!hasPermissions(userId, appId, draftType, relatedId)) {
      return completedFuture(notFound("Unknown relatedId " + relatedId));
    } else {
      try {
        draftDao.deleteFile(relatedId, fileId, DraftType.valueOf(fileType));
      } catch (DatabaseException databaseException) {
        LOGGER.error("Unable to delete file", databaseException);
        return completedFuture(badRequest());
      }
      return completedFuture(ok());
    }
  }

  private FileUploadResponse insertDraftAttachments(String appId, String relatedId, List<UploadFile> uploadFiles, DraftType draftType) {
    List<FileUploadResponseItem> fileUploadResponseItems = uploadFiles.stream()
        .map(uploadFile -> createFileUploadResponseItem(appId, relatedId, uploadFile, draftType))
        .collect(Collectors.toList());
    return new FileUploadResponse(fileUploadResponseItems);
  }

  private FileUploadResponseItem createFileUploadResponseItem(String appId, String relatedId, UploadFile uploadFile, DraftType draftType) {
    String name = uploadFile.getOriginalFilename();
    if (uploadFile.getProcessErrorInfo() != null) {
      return new FileUploadResponseItem(appId, name, null, uploadFile.getProcessErrorInfo(), null, relatedId, null, draftType.toString());
    } else {
      String size = FileUtil.getReadableFileSize(uploadFile.getDestinationPath());
      String fileId = createNewFile(relatedId, uploadFile, draftType);
      String link = getLink(appId, relatedId, fileId, draftType);
      return new FileUploadResponseItem(appId, name, link, null, size, relatedId, fileId, draftType.toString());
    }
  }

  private String getLink(String appId, String relatedId, String fileId, DraftType draftType) {
    if (draftType == DraftType.RFI_REPLY) {
      return routes.DownloadController.getRfiReplyFile(appId, relatedId, fileId).toString();
    } else {
      return routes.DownloadController.getAmendmentOrWithdrawalFile(relatedId, fileId).toString();
    }
  }

  private String createNewFile(String relatedId, UploadFile uploadFile, DraftType draftType) {
    File file = new File(fileId(), uploadFile.getOriginalFilename(), uploadFile.getDestinationPath());
    draftDao.addFile(relatedId, file, draftType);
    return file.getId();
  }

}

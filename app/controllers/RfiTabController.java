package controllers;

import static java.util.concurrent.CompletableFuture.completedFuture;

import com.google.inject.Inject;
import components.dao.DraftRfiResponseDao;
import components.dao.RfiResponseDao;
import components.exceptions.DatabaseException;
import components.service.ApplicationService;
import components.service.ApplicationSummaryViewService;
import components.service.RfiResponseService;
import components.service.RfiViewService;
import components.upload.UploadFile;
import components.upload.UploadMultipartParser;
import components.util.FileUtil;
import models.DraftRfiResponse;
import models.File;
import models.FileUploadResponse;
import models.FileUploadResponseItem;
import models.RfiResponse;
import models.view.AddRfiResponseView;
import models.view.ApplicationSummaryView;
import models.view.RfiView;
import models.view.form.RfiResponseForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.rfiListTab;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class RfiTabController extends Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(RfiTabController.class);

  private final String licenceApplicationAddress;
  private final FormFactory formFactory;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final RfiViewService rfiViewService;
  private final RfiResponseDao rfiResponseDao;
  private final ApplicationService applicationService;
  private final RfiResponseService rfiResponseService;
  private final DraftRfiResponseDao draftRfiResponseDao;

  @Inject
  public RfiTabController(String licenceApplicationAddress,
                          FormFactory formFactory,
                          ApplicationSummaryViewService applicationSummaryViewService,
                          RfiViewService rfiViewService,
                          RfiResponseDao rfiResponseDao,
                          ApplicationService applicationService,
                          RfiResponseService rfiResponseService,
                          DraftRfiResponseDao draftRfiResponseDao) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.formFactory = formFactory;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.rfiViewService = rfiViewService;
    this.rfiResponseDao = rfiResponseDao;
    this.applicationService = applicationService;
    this.rfiResponseService = rfiResponseService;
    this.draftRfiResponseDao = draftRfiResponseDao;
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public CompletionStage<Result> submitFiles(String rfiId) {
    List<UploadFile> files = getUploadFiles(request());
    FileUploadResponse fileUploadResponse = processFiles(rfiId, files);
    return completedFuture(ok(Json.toJson(fileUploadResponse)));
  }

  public CompletionStage<Result> deleteFile() {
    Map<String, String[]> formFields = request().body().asFormUrlEncoded();
    String rfiId = formFields.get("fileType")[0];
    String fileId = formFields.get("fileId")[0];
    try {
      draftRfiResponseDao.deleteFile(rfiId, fileId);
    } catch (DatabaseException databaseException) {
      LOGGER.error("Unable to delete file", databaseException);
      return completedFuture(badRequest());
    }
    return completedFuture(ok());

  }

  /**
   * Null check on 'uploadFile' is workaround for bug https://github.com/playframework/playframework/issues/6203
   */
  private static List<UploadFile> getUploadFiles(Http.Request request) {
    Http.MultipartFormData<UploadFile> body = request.body().asMultipartFormData();
    return body.getFiles().stream()
        .map(Http.MultipartFormData.FilePart::getFile)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private FileUploadResponse processFiles(String rfiId, List<UploadFile> uploadFiles) {
    List<FileUploadResponseItem> fileUploadResponseItems = uploadFiles.stream()
        .map(uploadFile -> createFileUploadResponseItem(rfiId, uploadFile))
        .collect(Collectors.toList());
    return new FileUploadResponse(fileUploadResponseItems);
  }

  private FileUploadResponseItem createFileUploadResponseItem(String rfiId, UploadFile uploadFile) {
    String name = uploadFile.getOriginalFilename();
    if (uploadFile.getProcessErrorInfo() != null) {
      return new FileUploadResponseItem(name, null, uploadFile.getProcessErrorInfo(), null, null, rfiId);
    } else {
      String size = FileUtil.getReadableFileSize(uploadFile.getDestinationPath());
      String id = createNewFile(rfiId, uploadFile);
      String link = controllers.routes.RfiTabController.getFile(rfiId, id).toString();
      return new FileUploadResponseItem(name, link, null, size, id, rfiId);
    }
  }

  private String createNewFile(String rfiId, UploadFile uploadFile) {
    File file = new File(UUID.randomUUID().toString(), uploadFile.getOriginalFilename(), uploadFile.getDestinationPath(), System.currentTimeMillis());
    draftRfiResponseDao.addFile(rfiId, file);
    return file.getFileId();
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public Result deleteFileById(String appId, String fileId) {
    Form<RfiResponseForm> rfiResponseForm = formFactory.form(RfiResponseForm.class).bindFromRequest();
    String rfiId = rfiResponseForm.data().get("rfiId");
    try {
      draftRfiResponseDao.deleteFile(rfiId, fileId);
    } catch (DatabaseException databaseException) {
      // Since this error could occur if the user refreshes the page, we do not return a bad request.
      LOGGER.warn("Unable to delete file.", databaseException);
    }
    rfiResponseForm.discardErrors();
    return showResponseForm(appId, rfiId, rfiResponseForm);
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public Result submitResponse(String appId) {
    Form<RfiResponseForm> rfiResponseForm = formFactory.form(RfiResponseForm.class).bindFromRequest();

    List<UploadFile> files = getUploadFiles(request());
    files.stream()
        .map(UploadFile::getProcessErrorInfo)
        .filter(Objects::nonNull)
        .forEach(rfiResponseForm::reject);

    String rfiId = rfiResponseForm.data().get("rfiId");
    if (alreadyHasResponse(rfiId)) {
      LOGGER.error("Response to rfiId {} and appId {} not possible since a response already exists", rfiId, appId);
      return showRfiTab(appId);
    } else if (!allowResponses(appId)) {
      LOGGER.error("Response to rfiId {} and appId {} not possible since application is complete.", rfiId, appId);
      return showRfiTab(appId);
    } else if (rfiResponseForm.hasErrors()) {
      return showResponseForm(appId, rfiId, rfiResponseForm);
    } else {
      String responseMessage = rfiResponseForm.get().responseMessage;
      rfiResponseService.insertRfiResponse(rfiId, responseMessage, files);
      flash("success", "Your message has been sent.");
      return redirect(controllers.routes.RfiTabController.showRfiTab(appId));
    }
  }

  public Result showResponseForm(String appId, String rfiId) {
    if (alreadyHasResponse(rfiId)) {
      LOGGER.error("Response to rfiId {} and appId {} not possible since a response already exists", rfiId);
      return showRfiTab(appId);
    } else if (!allowResponses(appId)) {
      LOGGER.error("Response to rfiId {} and appId {} not possible since application is complete.", rfiId, appId);
      return showRfiTab(appId);
    } else {
      RfiResponseForm rfiResponseForm = new RfiResponseForm();
      rfiResponseForm.rfiId = rfiId;
      Form<RfiResponseForm> form = formFactory.form(RfiResponseForm.class).fill(rfiResponseForm);
      return showResponseForm(appId, rfiId, form);
    }
  }

  private Result showResponseForm(String appId, String rfiId, Form<RfiResponseForm> rfiResponseForm) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    List<RfiView> rfiViews = rfiViewService.getRfiViews(appId);
    AddRfiResponseView addRfiResponseView = rfiViewService.getAddRfiResponseView(rfiId);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, rfiViews, allowResponses(appId), rfiResponseForm, addRfiResponseView));
  }

  public Result showRfiTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    List<RfiView> rfiViews = rfiViewService.getRfiViews(appId);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, rfiViews, allowResponses(appId), null, null));
  }

  public Result getFile(String rfiId, String fileId) {
    List<File> files;
    RfiResponse rfiResponse = rfiResponseDao.getRfiResponse(rfiId);
    DraftRfiResponse draftRfiResponse = draftRfiResponseDao.getDraftRfiResponse(rfiId);
    if (rfiResponse != null) {
      files = rfiResponse.getAttachments();
    } else if (draftRfiResponse != null) {
      files = draftRfiResponse.getAttachments();
    } else {
      files = new ArrayList<>();
    }
    Optional<File> file = files.stream()
        .filter(f -> f.getFileId().equals(fileId))
        .findAny();
    if (file.isPresent()) {
      return ok(new java.io.File(file.get().getPath()));
    } else {
      LOGGER.warn("No file found with rfiId {} and fileId {}", rfiId, fileId);
      return notFound();
    }
  }

  private boolean alreadyHasResponse(String rfiId) {
    return rfiResponseDao.getRfiResponse(rfiId) != null;
  }

  private boolean allowResponses(String appId) {
    return applicationService.isApplicationInProgress(appId);
  }

}

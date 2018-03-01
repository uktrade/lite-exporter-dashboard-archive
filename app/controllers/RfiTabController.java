package controllers;

import static java.util.concurrent.CompletableFuture.completedFuture;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.common.upload.FileService;
import components.common.upload.FileUtil;
import components.common.upload.UploadMultipartParser;
import components.common.upload.UploadResult;
import components.common.upload.UploadValidationConfig;
import components.dao.DraftFileDao;
import components.service.AppDataService;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationTabsViewService;
import components.service.DraftFileService;
import components.service.ReadDataService;
import components.service.RfiReplyService;
import components.service.RfiViewService;
import components.service.UserPermissionService;
import components.service.UserService;
import models.AppData;
import models.ReadData;
import models.enums.DraftType;
import models.view.AddRfiReplyView;
import models.view.ApplicationSummaryView;
import models.view.ApplicationTabsView;
import models.view.RfiView;
import models.view.form.RfiReplyForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.With;
import views.html.rfiListTab;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@With(AppGuardAction.class)
public class RfiTabController extends SamlController {

  private static final Logger LOGGER = LoggerFactory.getLogger(RfiTabController.class);

  private final String licenceApplicationAddress;
  private final FormFactory formFactory;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final RfiViewService rfiViewService;
  private final RfiReplyService rfiReplyService;
  private final UserService userService;
  private final AppDataService appDataService;
  private final ApplicationTabsViewService applicationTabsViewService;
  private final ReadDataService readDataService;
  private final UserPermissionService userPermissionService;
  private final FileService fileService;
  private final DraftFileDao draftFileDao;
  private final DraftFileService draftFileService;
  private final HttpExecutionContext context;
  private final UploadValidationConfig uploadValidationConfig;

  @Inject
  public RfiTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                          FormFactory formFactory,
                          ApplicationSummaryViewService applicationSummaryViewService,
                          RfiViewService rfiViewService,
                          RfiReplyService rfiReplyService,
                          UserService userService,
                          AppDataService appDataService,
                          ApplicationTabsViewService applicationTabsViewService,
                          ReadDataService readDataService,
                          UserPermissionService userPermissionService,
                          FileService fileService,
                          DraftFileDao draftFileDao,
                          DraftFileService draftFileService,
                          HttpExecutionContext httpExecutionContext,
                          UploadValidationConfig uploadValidationConfig) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.formFactory = formFactory;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.rfiViewService = rfiViewService;
    this.rfiReplyService = rfiReplyService;
    this.userService = userService;
    this.appDataService = appDataService;
    this.applicationTabsViewService = applicationTabsViewService;
    this.readDataService = readDataService;
    this.userPermissionService = userPermissionService;
    this.fileService = fileService;
    this.draftFileDao = draftFileDao;
    this.draftFileService = draftFileService;
    this.context = httpExecutionContext;
    this.uploadValidationConfig = uploadValidationConfig;
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public CompletionStage<Result> submitReply(String appId, String rfiId) {
    String userId = userService.getCurrentUserId();
    Form<RfiReplyForm> rfiReplyForm = formFactory.form(RfiReplyForm.class).bindFromRequest();
    String delete = rfiReplyForm.data().get("delete");
    AppData appData = appDataService.getAppData(appId);
    if (!userPermissionService.canAddRfiReply(userId, rfiId, appData)) {
      LOGGER.error("Reply to rfiId {} and appId {} not allowed", rfiId, appId);
      return completedFuture(showRfiTab(appId));
    } else if (delete != null) {
      draftFileService.deleteDraftFile(delete, rfiId, DraftType.RFI_REPLY);
      return CompletableFuture.completedFuture(showReplyForm(appId, rfiId, rfiReplyForm));
    } else {
      return fileService.processUpload(appId, request())
          .thenApplyAsync(uploadResults -> {
            uploadResults.stream()
                .filter(UploadResult::isValid)
                .forEach(uploadResult -> draftFileDao.addDraftFile(uploadResult, rfiId, DraftType.RFI_REPLY));
            Form<RfiReplyForm> form = FileUtil.addUploadErrorsToForm(rfiReplyForm, uploadResults);
            if (form.hasErrors()) {
              return showReplyForm(appId, rfiId, form);
            } else {
              String message = form.get().replyMessage;
              rfiReplyService.insertRfiReply(userId, appId, rfiId, message);
              flash("message", "Your message has been sent");
              return redirect(controllers.routes.RfiTabController.showRfiTab(appId));
            }
          }, context.current());
    }
  }

  public Result showReplyForm(String appId, String rfiId) {
    FileUtil.addFlash(request(), uploadValidationConfig);
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    if (!userPermissionService.canAddRfiReply(userId, rfiId, appData)) {
      LOGGER.error("Reply to rfiId {} and appId {} not allowed", rfiId, appId);
      return showRfiTab(appId);
    } else {
      Form<RfiReplyForm> form = formFactory.form(RfiReplyForm.class);
      return showReplyForm(appId, rfiId, form);
    }
  }

  private Result showReplyForm(String appId, String rfiId, Form<RfiReplyForm> rfiReplyForm) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    ReadData readData = readDataService.getReadData(userId, appData);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appData);
    ApplicationTabsView applicationTabsView = applicationTabsViewService.getApplicationTabsView(appData, readData);
    List<RfiView> rfiViews = rfiViewService.getRfiViews(userId, appData);
    AddRfiReplyView addRfiReplyView = rfiViewService.getAddRfiReplyView(appId, rfiId);
    readDataService.updateRfiTabReadData(userId, appData, readData);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, applicationTabsView, rfiViews, rfiReplyForm, addRfiReplyView)).withHeader("Cache-Control", "no-store, no-cache");
  }

  public Result showRfiTab(String appId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    ReadData readData = readDataService.getReadData(userId, appData);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appData);
    ApplicationTabsView applicationTabsView = applicationTabsViewService.getApplicationTabsView(appData, readData);
    List<RfiView> rfiViews = rfiViewService.getRfiViews(userId, appData);
    readDataService.updateRfiTabReadData(userId, appData, readData);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, applicationTabsView, rfiViews, null, null)).withHeader("Cache-Control", "no-store, no-cache");
  }

}

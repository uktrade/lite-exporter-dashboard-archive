package controllers;

import static java.util.concurrent.CompletableFuture.completedFuture;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.common.upload.FileService;
import components.common.upload.FileUtil;
import components.common.upload.UploadMultipartParser;
import components.common.upload.UploadResult;
import components.dao.DraftFileDao;
import components.service.AmendmentService;
import components.service.AppDataService;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationTabsViewService;
import components.service.DraftFileService;
import components.service.OfficerViewService;
import components.service.PreviousRequestItemViewService;
import components.service.ReadDataService;
import components.service.UserPermissionService;
import components.service.UserService;
import components.service.WithdrawalRequestService;
import components.util.ApplicationUtil;
import components.util.EnumUtil;
import models.AppData;
import models.Attachment;
import models.ReadData;
import models.enums.Action;
import models.enums.DraftType;
import models.view.AmendmentView;
import models.view.ApplicationSummaryView;
import models.view.ApplicationTabsView;
import models.view.FileView;
import models.view.OfficerView;
import models.view.PreviousRequestItemView;
import models.view.form.AmendApplicationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.With;
import utils.common.SelectOption;
import views.html.amendApplicationTab;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@With(AppGuardAction.class)
public class AmendTabController extends SamlController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmendTabController.class);

  private final String licenceApplicationAddress;
  private final FormFactory formFactory;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final OfficerViewService officerViewService;
  private final AmendmentService amendmentService;
  private final WithdrawalRequestService withdrawalRequestService;
  private final DraftFileDao draftFileDao;
  private final UserService userService;
  private final AppDataService appDataService;
  private final ApplicationTabsViewService applicationTabsViewService;
  private final ReadDataService readDataService;
  private final UserPermissionService userPermissionService;
  private final PreviousRequestItemViewService previousRequestItemViewService;
  private final FileService fileService;
  private final DraftFileService draftFileService;
  private final HttpExecutionContext context;

  @Inject
  public AmendTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                            FormFactory formFactory,
                            ApplicationSummaryViewService applicationSummaryViewService,
                            OfficerViewService officerViewService,
                            AmendmentService amendmentService,
                            WithdrawalRequestService withdrawalRequestService,
                            DraftFileDao draftFileDao,
                            UserService userService,
                            AppDataService appDataService,
                            ApplicationTabsViewService applicationTabsViewService,
                            ReadDataService readDataService,
                            UserPermissionService userPermissionService,
                            PreviousRequestItemViewService previousRequestItemViewService,
                            FileService fileService,
                            DraftFileService draftFileService,
                            HttpExecutionContext httpExecutionContext) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.formFactory = formFactory;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.officerViewService = officerViewService;
    this.amendmentService = amendmentService;
    this.withdrawalRequestService = withdrawalRequestService;
    this.draftFileDao = draftFileDao;
    this.userService = userService;
    this.appDataService = appDataService;
    this.applicationTabsViewService = applicationTabsViewService;
    this.readDataService = readDataService;
    this.userPermissionService = userPermissionService;
    this.previousRequestItemViewService = previousRequestItemViewService;
    this.fileService = fileService;
    this.draftFileService = draftFileService;
    this.context = httpExecutionContext;
  }

  public Result deleteFileById(String appId, String fileId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    Form<AmendApplicationForm> amendApplicationForm = formFactory.form(AmendApplicationForm.class).bindFromRequest();
    if (!userPermissionService.canAddAmendmentOrWithdrawalRequest(userId, appData)) {
      LOGGER.error("Unable to delete file with id {} since amending application with id {} not allowed.", fileId, appId);
      return showAmendTab(appId);
    } else {
      draftFileService.deleteDraftFile(fileId, appId, DraftType.AMENDMENT_OR_WITHDRAWAL);
      amendApplicationForm.discardErrors();
      return showAmendTab(appId, amendApplicationForm);
    }
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public CompletionStage<Result> submitAmendment(String appId) {
    String userId = userService.getCurrentUserId();
    Form<AmendApplicationForm> amendApplicationForm = formFactory.form(AmendApplicationForm.class).bindFromRequest();
    String actionParam = amendApplicationForm.data().get("action");
    Action action = EnumUtil.parse(actionParam, Action.class);
    AppData appData = appDataService.getAppData(appId);
    if (!userPermissionService.canAddAmendmentOrWithdrawalRequest(userId, appData)) {
      LOGGER.error("Amending application with appId {} and action {} not possible since amendment not allowed.", appId, action);
      return completedFuture(showAmendTab(appId));
    } else if (action == null) {
      LOGGER.error("Amending application with appId {} and action {} not possible", appId, actionParam);
      return completedFuture(showAmendTab(appId));
    } else {
      return fileService.processUpload(appId, request())
          .thenApplyAsync(uploadResults -> {
            FileUtil.addUploadErrorsToForm(amendApplicationForm, uploadResults);
            uploadResults.stream()
                .filter(UploadResult::isValid)
                .forEach(uploadResult -> draftFileDao.addDraftFile(uploadResult, appId, DraftType.AMENDMENT_OR_WITHDRAWAL));
            if (amendApplicationForm.hasErrors()) {
              return showAmendTab(appId, amendApplicationForm);
            } else {
              String message = amendApplicationForm.get().message;
              if (action == Action.AMEND) {
                amendmentService.insertAmendment(userId, appId, message);
                flash("message", "Your amendment request has been sent");
                flash("detail", "A case officer will deal with it shortly");
              } else if (action == Action.WITHDRAW) {
                withdrawalRequestService.insertWithdrawalRequest(userId, appId, message);
                flash("message", "Your withdrawal request has been sent");
                flash("detail", "A case officer will deal with it shortly. You cannot make any further withdrawal or amendment " +
                    "requests while this one is pending");
              }
              return redirect(routes.AmendTabController.showAmendTab(appId));
            }
          }, context.current());
    }
  }

  public Result showAmendTab(String appId) {
    Form<AmendApplicationForm> form = formFactory.form(AmendApplicationForm.class);
    return showAmendTab(appId, form);
  }

  private Result showAmendTab(String appId, Form<AmendApplicationForm> form) {
    AppData appData = appDataService.getAppData(appId);
    String userId = userService.getCurrentUserId();
    ReadData readData = readDataService.getReadData(userId, appData);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appData);
    ApplicationTabsView applicationTabsView = applicationTabsViewService.getApplicationTabsView(appData, readData);
    OfficerView officerView = officerViewService.getOfficerView(appData.getApplication().getCaseOfficerId());
    List<FileView> fileViews = createFileViews(appId);
    List<SelectOption> selectOptions = getSelectOptions();
    List<PreviousRequestItemView> previousRequestItemViews = previousRequestItemViewService.getPreviousRequestItemViews(appData);
    boolean hasPendingWithdrawalRequest = ApplicationUtil.hasPendingWithdrawalRequest(appData);
    boolean applicationInProgress = ApplicationUtil.isOriginalApplicationInProgress(appData);
    boolean hasCreatorOrAdminPermission = userPermissionService.hasCreatorOrAdminPermission(userId, appData);
    AmendmentView amendmentView = new AmendmentView(applicationInProgress,
        hasPendingWithdrawalRequest,
        hasCreatorOrAdminPermission,
        previousRequestItemViews,
        selectOptions,
        fileViews,
        officerView);
    return ok(amendApplicationTab.render(licenceApplicationAddress,
        applicationSummaryView,
        applicationTabsView,
        amendmentView,
        form))
        .withHeader("Cache-Control", "no-store");
  }

  private List<FileView> createFileViews(String appId) {
    List<Attachment> attachments = draftFileDao.getAttachments(appId, DraftType.AMENDMENT_OR_WITHDRAWAL);
    return attachments.stream()
        .map(attachment -> createFileView(appId, attachment))
        .collect(Collectors.toList());
  }

  private FileView createFileView(String appId, Attachment attachment) {
    String link = routes.DownloadController.getAmendmentOrWithdrawalAttachment(appId, attachment.getId()).toString();
    String deleteLink = routes.AmendTabController.deleteFileById(appId, attachment.getId()).toString();
    String size = FileUtil.getReadableFileSize(attachment.getSize());
    return new FileView(attachment.getId(), appId, appId, attachment.getFilename(), link, deleteLink, size);
  }

  private List<SelectOption> getSelectOptions() {
    SelectOption amend = new SelectOption("amend", "Amend your application");
    SelectOption withdraw = new SelectOption("withdraw", "Withdraw your application");
    return Arrays.asList(amend, withdraw);
  }

}

package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.dao.DraftDao;
import components.exceptions.DatabaseException;
import components.service.AmendmentService;
import components.service.AppDataService;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationTabsViewService;
import components.service.OfficerViewService;
import components.service.PreviousRequestItemViewService;
import components.service.ReadDataService;
import components.service.UserPrivilegeService;
import components.service.UserService;
import components.service.WithdrawalRequestService;
import components.upload.UploadFile;
import components.upload.UploadMultipartParser;
import components.util.ApplicationUtil;
import components.util.EnumUtil;
import components.util.FileUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import models.AppData;
import models.File;
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
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.With;
import utils.common.SelectOption;
import views.html.amendApplicationTab;

@With(AppGuardAction.class)
public class AmendTabController extends SamlController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmendTabController.class);

  private final String licenceApplicationAddress;
  private final FormFactory formFactory;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final OfficerViewService officerViewService;
  private final AmendmentService amendmentService;
  private final WithdrawalRequestService withdrawalRequestService;
  private final DraftDao draftDao;
  private final UserService userService;
  private final AppDataService appDataService;
  private final ApplicationTabsViewService applicationTabsViewService;
  private final ReadDataService readDataService;
  private final UserPrivilegeService userPrivilegeService;
  private final PreviousRequestItemViewService previousRequestItemViewService;

  @Inject
  public AmendTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                            FormFactory formFactory,
                            ApplicationSummaryViewService applicationSummaryViewService,
                            OfficerViewService officerViewService,
                            AmendmentService amendmentService,
                            WithdrawalRequestService withdrawalRequestService,
                            DraftDao draftDao,
                            UserService userService,
                            AppDataService appDataService,
                            ApplicationTabsViewService applicationTabsViewService,
                            ReadDataService readDataService,
                            UserPrivilegeService userPrivilegeService,
                            PreviousRequestItemViewService previousRequestItemViewService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.formFactory = formFactory;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.officerViewService = officerViewService;
    this.amendmentService = amendmentService;
    this.withdrawalRequestService = withdrawalRequestService;
    this.draftDao = draftDao;
    this.userService = userService;
    this.appDataService = appDataService;
    this.applicationTabsViewService = applicationTabsViewService;
    this.readDataService = readDataService;
    this.userPrivilegeService = userPrivilegeService;
    this.previousRequestItemViewService = previousRequestItemViewService;
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public Result deleteFileById(String appId, String fileId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    Form<AmendApplicationForm> amendApplicationForm = formFactory.form(AmendApplicationForm.class).bindFromRequest();
    if (!userPrivilegeService.isAmendmentOrWithdrawalAllowed(userId, appData)) {
      LOGGER.error("Unable to delete file with id {} since amending application with id {} not allowed.", fileId, appId);
      return showAmendTab(appId);
    } else {
      try {
        draftDao.deleteFile(appId, fileId, DraftType.AMENDMENT_OR_WITHDRAWAL);
      } catch (DatabaseException databaseException) {
        // Since this error could occur if the user refreshes the page, we do not return a bad request.
        LOGGER.warn("Unable to delete file.", databaseException);
      }
      amendApplicationForm.discardErrors();
      return showAmendTab(appId, amendApplicationForm);
    }
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public Result submitAmendment(String appId) {
    String userId = userService.getCurrentUserId();
    Form<AmendApplicationForm> amendApplicationForm = formFactory.form(AmendApplicationForm.class).bindFromRequest();
    String actionParam = amendApplicationForm.data().get("action");
    Action action = EnumUtil.parse(actionParam, Action.class);
    AppData appData = appDataService.getAppData(appId);
    List<UploadFile> uploadFiles = FileUtil.getUploadFiles(request());
    FileUtil.processErrors(amendApplicationForm, uploadFiles);
    if (action == null) {
      LOGGER.error("Amending application with appId {} and action {} not possible", appId, actionParam);
      return showAmendTab(appId);
    } else if (!userPrivilegeService.isAmendmentOrWithdrawalAllowed(userId, appData)) {
      LOGGER.error("Amending application with appId {} and action {} not possible since amendment not allowed.", appId, action);
      return showAmendTab(appId);
    } else if (amendApplicationForm.hasErrors()) {
      return showAmendTab(appId, amendApplicationForm);
    } else {
      String message = amendApplicationForm.get().message;
      if (action == Action.AMEND) {
        amendmentService.insertAmendment(userId, appId, message, uploadFiles);
        flash("success", "Your amendment request has been sent<br>A case officer will deal with it shortly");
      } else if (action == Action.WITHDRAW) {
        withdrawalRequestService.insertWithdrawalRequest(userId, appId, message, uploadFiles);
        flash("success", "Your withdrawal request has been sent<br>A case officer will deal with it shortly. "
            + "You cannot make any further withdrawal or amendment requests while this one is pending.");
      }
      return redirect(routes.AmendTabController.showAmendTab(appId));
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
    OfficerView officerView = officerViewService.getOfficerView(appId);
    List<FileView> fileViews = createFileViews(appId);
    List<SelectOption> selectOptions = getSelectOptions();
    List<PreviousRequestItemView> previousRequestItemViews = previousRequestItemViewService.getPreviousRequestItemViews(appData);
    boolean hasPendingWithdrawalRequest = ApplicationUtil.hasPendingWithdrawalRequest(appData);
    boolean applicationInProgress = ApplicationUtil.isApplicationInProgress(appData);
    boolean hasCreatorOrAdminPermission = userPrivilegeService.hasCreatorOrAdminPermission(userId, appData);
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
    List<File> files = draftDao.getDraftAttachments(appId, DraftType.AMENDMENT_OR_WITHDRAWAL);
    return files.stream()
        .map(file -> createFileView(appId, file))
        .collect(Collectors.toList());
  }

  private FileView createFileView(String appId, File file) {
    String link = routes.DownloadController.getAmendmentOrWithdrawalFile(appId, file.getId()).toString();
    String deleteLink = routes.AmendTabController.deleteFileById(appId, file.getId()).toString();
    return new FileView(file.getId(), appId, appId, file.getFilename(), link, deleteLink, FileUtil.getReadableFileSize(file.getUrl()));
  }

  private List<SelectOption> getSelectOptions() {
    SelectOption amend = new SelectOption("amend", "Amend your application");
    SelectOption withdraw = new SelectOption("withdraw", "Withdraw your application");
    return Arrays.asList(amend, withdraw);
  }

}

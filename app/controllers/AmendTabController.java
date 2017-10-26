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
import components.service.ReadDataService;
import components.service.UserService;
import components.service.WithdrawalRequestService;
import components.upload.UploadFile;
import components.upload.UploadMultipartParser;
import components.util.ApplicationUtil;
import components.util.Comparators;
import components.util.EnumUtil;
import components.util.FileUtil;
import components.util.LinkUtil;
import components.util.TimeUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.AppData;
import models.File;
import models.ReadData;
import models.WithdrawalRejection;
import models.WithdrawalRequest;
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
                            ReadDataService readDataService) {
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
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public Result deleteFileById(String appId, String fileId) {
    AppData appData = appDataService.getAppData(appId);
    Form<AmendApplicationForm> amendApplicationForm = formFactory.form(AmendApplicationForm.class).bindFromRequest();
    if (!allowAmendment(appData)) {
      LOGGER.warn("Unable to delete file with id {} since amending application with id {} not allowed.", fileId, appId);
    } else {
      try {
        draftDao.deleteFile(appId, fileId, DraftType.AMENDMENT_OR_WITHDRAWAL);
      } catch (DatabaseException databaseException) {
        // Since this error could occur if the user refreshes the page, we do not return a bad request.
        LOGGER.warn("Unable to delete file.", databaseException);
      }
      amendApplicationForm.discardErrors();
    }
    return showAmendTab(appId, amendApplicationForm);
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public Result submitAmendment(String appId) {
    Form<AmendApplicationForm> amendApplicationForm = formFactory.form(AmendApplicationForm.class).bindFromRequest();
    String actionParam = amendApplicationForm.data().get("action");
    Action action = EnumUtil.parse(actionParam, Action.class);
    AppData appData = appDataService.getAppData(appId);
    List<UploadFile> uploadFiles = FileUtil.getUploadFiles(request());
    FileUtil.processErrors(amendApplicationForm, uploadFiles);
    if (action == null) {
      LOGGER.error("Amending application with appId {} and action {} not possible", appId, actionParam);
      return showAmendTab(appId);
    } else if (!allowAmendment(appData)) {
      LOGGER.error("Amending application with appId {} and action {} not possible since amendment not allowed.", appId, action);
      return showAmendTab(appId);
    } else if (amendApplicationForm.hasErrors()) {
      return showAmendTab(appId, amendApplicationForm);
    } else {
      String userId = userService.getCurrentUserId();
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
    SelectOption amend = new SelectOption("amend", "Amend your application");
    SelectOption withdraw = new SelectOption("withdraw", "Withdraw your application");
    List<SelectOption> selectOptions = Arrays.asList(amend, withdraw);
    List<PreviousRequestItemView> previousRequestItemViews = getPreviousRequestItemViews(appData);
    boolean applicationInProgress = ApplicationUtil.isApplicationInProgress(appData);
    boolean hasPendingWithdrawalRequest = appData.getWithdrawalApproval() == null && appData.getWithdrawalRequests().size() > appData.getWithdrawalRejections().size();
    AmendmentView amendmentView = new AmendmentView(applicationInProgress, hasPendingWithdrawalRequest, previousRequestItemViews, selectOptions, fileViews, officerView);
    return ok(amendApplicationTab.render(licenceApplicationAddress,
        applicationSummaryView,
        applicationTabsView,
        amendmentView,
        form))
        .withHeader("Cache-Control", "no-store");
  }

  private boolean allowAmendment(AppData appData) {
    return ApplicationUtil.isApplicationInProgress(appData) && !(appData.getWithdrawalRequests().size() > appData.getWithdrawalRejections().size());
  }

  private List<FileView> createFileViews(String appId) {
    List<File> files = draftDao.getDraftAttachments(appId, DraftType.AMENDMENT_OR_WITHDRAWAL);
    return files.stream()
        .map(file -> createFileView(appId, file))
        .collect(Collectors.toList());
  }

  private FileView createFileView(String appId, File file) {
    String link = routes.DownloadController.getFile(appId, file.getId()).toString();
    String deleteLink = routes.AmendTabController.deleteFileById(appId, file.getId()).toString();
    return new FileView(file.getId(), appId, file.getFilename(), link, deleteLink, FileUtil.getReadableFileSize(file.getUrl()));
  }

  private List<PreviousRequestItemView> getPreviousRequestItemViews(AppData appData) {
    List<PreviousRequestItemView> previousRequestItemViews = new ArrayList<>();
    appData.getAmendments().stream()
        .map(amendment -> {
          String date = TimeUtil.formatDate(amendment.getCreatedTimestamp());
          Long createdTimestamp = amendment.getCreatedTimestamp();
          String type = "Amendment";
          String link = LinkUtil.getAmendmentMessageLink(amendment);
          return new PreviousRequestItemView(createdTimestamp, date, type, link, null);
        }).forEach(previousRequestItemViews::add);
    Map<String, WithdrawalRejection> withdrawalRejectionMap = ApplicationUtil.getWithdrawalRejectionMap(appData);
    WithdrawalRequest approvedWithdrawalRequest = ApplicationUtil.getApprovedWithdrawalRequest(appData);
    appData.getWithdrawalRequests().stream()
        .map(withdrawalRequest -> {
          String date = TimeUtil.formatDate(withdrawalRequest.getCreatedTimestamp());
          Long createdTimestamp = withdrawalRequest.getCreatedTimestamp();
          String type = "Withdrawal";
          String link = LinkUtil.getWithdrawalRequestMessageLink(withdrawalRequest);
          String indicator;
          if (withdrawalRejectionMap.get(withdrawalRequest.getId()) != null) {
            indicator = "rejected";
          } else if (approvedWithdrawalRequest != null && approvedWithdrawalRequest.getId().equals(withdrawalRequest.getId())) {
            indicator = "approved";
          } else {
            indicator = null;
          }
          return new PreviousRequestItemView(createdTimestamp, date, type, link, indicator);
        }).forEach(previousRequestItemViews::add);
    previousRequestItemViews.sort(Comparators.PREVIOUS_REQUEST_ITEM_VIEW_CREATED_REVERSED);
    return previousRequestItemViews;
  }

}

package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.dao.DraftDao;
import components.exceptions.DatabaseException;
import components.exceptions.UnexpectedStateException;
import components.service.AmendmentService;
import components.service.ApplicationService;
import components.service.ApplicationSummaryViewService;
import components.service.OfficerViewService;
import components.service.RfiViewService;
import components.service.UserService;
import components.service.WithdrawalRequestService;
import components.upload.UploadFile;
import components.upload.UploadMultipartParser;
import components.util.EnumUtil;
import components.util.FileUtil;
import models.enums.Action;
import models.enums.DraftType;
import uk.gov.bis.lite.exporterdashboard.api.File;
import models.view.AddAmendmentView;
import models.view.ApplicationSummaryView;
import models.view.FileView;
import models.view.OfficerView;
import models.view.form.AmendApplicationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.BodyParser;
import play.mvc.Result;
import views.html.amendApplicationTab;

import java.util.List;
import java.util.stream.Collectors;

public class AmendTabController extends SamlController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmendTabController.class);

  private final String licenceApplicationAddress;
  private final FormFactory formFactory;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final OfficerViewService officerViewService;
  private final RfiViewService rfiViewService;
  private final ApplicationService applicationService;
  private final AmendmentService amendmentService;
  private final WithdrawalRequestService withdrawalRequestService;
  private final DraftDao draftDao;
  private final UserService userService;

  @Inject
  public AmendTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                            FormFactory formFactory,
                            ApplicationSummaryViewService applicationSummaryViewService,
                            OfficerViewService officerViewService,
                            RfiViewService rfiViewService,
                            ApplicationService applicationService,
                            AmendmentService amendmentService,
                            WithdrawalRequestService withdrawalRequestService,
                            DraftDao draftDao,
                            UserService userService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.formFactory = formFactory;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.officerViewService = officerViewService;
    this.rfiViewService = rfiViewService;
    this.applicationService = applicationService;
    this.amendmentService = amendmentService;
    this.withdrawalRequestService = withdrawalRequestService;
    this.draftDao = draftDao;
    this.userService = userService;
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public Result deleteFileById(String appId, String fileId) {
    Form<AmendApplicationForm> amendApplicationForm = formFactory.form(AmendApplicationForm.class).bindFromRequest();
    String actionParam = amendApplicationForm.data().get("action");
    Action action = EnumUtil.parse(actionParam, Action.class);
    DraftType draftType = toDraftType(action);
    try {
      draftDao.deleteFile(appId, fileId, draftType);
    } catch (DatabaseException databaseException) {
      // Since this error could occur if the user refreshes the page, we do not return a bad request.
      LOGGER.warn("Unable to delete file.", databaseException);
    }
    amendApplicationForm.discardErrors();
    return showAmendTab(appId, action, amendApplicationForm);
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public Result submitAmendment(String appId) {
    Form<AmendApplicationForm> amendApplicationForm = formFactory.form(AmendApplicationForm.class).bindFromRequest();
    String actionParam = amendApplicationForm.data().get("action");
    Action action = EnumUtil.parse(actionParam, Action.class);

    List<UploadFile> uploadFiles = FileUtil.getUploadFiles(request());
    FileUtil.processErrors(amendApplicationForm, uploadFiles);

    if (action == null) {
      LOGGER.error("Amending application with action {} not possible", actionParam);
      return showAmendTab(appId, null, null);
    } else if (!allowAmendment(appId)) {
      LOGGER.error("Amending application with appId {} and action {} not possible since application is complete.", appId, action);
      return showAmendTab(appId, null, null);
    } else if (amendApplicationForm.hasErrors()) {
      return showAmendTab(appId, action, amendApplicationForm);
    } else {
      String userId = userService.getCurrentUserId();
      String message = amendApplicationForm.get().message;
      if (action == Action.AMEND) {
        amendmentService.insertAmendment(userId, appId, message, uploadFiles);
      } else if (action == Action.WITHDRAW) {
        withdrawalRequestService.insertWithdrawalRequest(userId, appId, message, uploadFiles);
      }
      return showAmendTab(appId, null, null);
    }
  }

  public Result showAmendTab(String appId, String actionParam) {
    Action action = EnumUtil.parse(actionParam, Action.class);
    if (action == null) {
      return showAmendTab(appId, null, null);
    } else if (!allowAmendment(appId)) {
      LOGGER.error("Amending application with appId {} and action {} not possible since application is complete.", appId, actionParam);
      return showAmendTab(appId, null, null);
    } else {
      AmendApplicationForm amendApplicationForm = new AmendApplicationForm();
      amendApplicationForm.action = action.toString();
      Form<AmendApplicationForm> form = formFactory.form(AmendApplicationForm.class).fill(amendApplicationForm);
      return showAmendTab(appId, action, form);
    }
  }

  private Result showAmendTab(String appId, Action action, Form<AmendApplicationForm> form) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    OfficerView officerView = officerViewService.getOfficerView(appId);
    int rfiViewCount = rfiViewService.getRfiViewCount(appId);
    AddAmendmentView addAmendmentView;
    if (action != null) {
      DraftType draftType = toDraftType(action);
      List<FileView> fileViews = createFileViews(appId, draftType);
      addAmendmentView = new AddAmendmentView(draftType, fileViews);
    } else {
      addAmendmentView = null;
    }
    return ok(amendApplicationTab.render(licenceApplicationAddress,
        applicationSummaryView,
        rfiViewCount,
        allowAmendment(appId),
        action,
        form,
        officerView,
        addAmendmentView));
  }

  private boolean allowAmendment(String appId) {
    return applicationService.isApplicationInProgress(appId);
  }

  private List<FileView> createFileViews(String appId, DraftType draftType) {
    List<File> files = draftDao.getDraftAttachments(appId, draftType);
    return files.stream()
        .map(file -> createFileView(appId, file, draftType))
        .collect(Collectors.toList());
  }

  private FileView createFileView(String appId, File file, DraftType draftType) {
    String link = getLink(appId, file.getId(), draftType);
    String deleteLink = routes.AmendTabController.deleteFileById(appId, file.getId()).toString();
    return new FileView(file.getId(), appId, file.getFilename(), link, deleteLink, FileUtil.getReadableFileSize(file.getUrl()));
  }

  private String getLink(String appId, String fileId, DraftType draftType) {
    switch (draftType) {
      case AMENDMENT:
        return routes.DownloadController.getAmendmentFile(appId, fileId).toString();
      case WITHDRAWAL:
        return routes.DownloadController.getWithdrawalFile(appId, fileId).toString();
      default:
        throw new UnexpectedStateException("Unexpected draft type " + draftType);
    }
  }

  private DraftType toDraftType(Action action) {
    if (action == Action.AMEND) {
      return DraftType.AMENDMENT;
    } else if (action == Action.WITHDRAW) {
      return DraftType.WITHDRAWAL;
    } else {
      return null;
    }
  }

}

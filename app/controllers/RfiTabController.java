package controllers;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.dao.RfiReplyDao;
import components.exceptions.DatabaseException;
import components.service.AppDataService;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationTabsViewService;
import components.service.ReadDataService;
import components.service.RfiReplyService;
import components.service.RfiViewService;
import components.service.UserPrivilegeService;
import components.service.UserService;
import components.upload.UploadFile;
import components.upload.UploadMultipartParser;
import components.util.FileUtil;
import java.util.List;
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
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.With;
import views.html.rfiListTab;

@With(AppGuardAction.class)
public class RfiTabController extends SamlController {

  private static final Logger LOGGER = LoggerFactory.getLogger(RfiTabController.class);

  private final String licenceApplicationAddress;
  private final FormFactory formFactory;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final RfiViewService rfiViewService;
  private final RfiReplyDao rfiReplyDao;
  private final RfiReplyService rfiReplyService;
  private final DraftDao draftDao;
  private final UserService userService;
  private final AppDataService appDataService;
  private final ApplicationTabsViewService applicationTabsViewService;
  private final ReadDataService readDataService;
  private final UserPrivilegeService userPrivilegeService;

  @Inject
  public RfiTabController(String licenceApplicationAddress,
                          FormFactory formFactory,
                          ApplicationSummaryViewService applicationSummaryViewService,
                          RfiViewService rfiViewService,
                          RfiReplyDao rfiReplyDao,
                          RfiReplyService rfiReplyService,
                          DraftDao draftDao, UserService userService,
                          AppDataService appDataService,
                          ApplicationTabsViewService applicationTabsViewService,
                          ReadDataService readDataService,
                          UserPrivilegeService userPrivilegeService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.formFactory = formFactory;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.rfiViewService = rfiViewService;
    this.rfiReplyDao = rfiReplyDao;
    this.rfiReplyService = rfiReplyService;
    this.draftDao = draftDao;
    this.userService = userService;
    this.appDataService = appDataService;
    this.applicationTabsViewService = applicationTabsViewService;
    this.readDataService = readDataService;
    this.userPrivilegeService = userPrivilegeService;
  }

  public Result deleteFileById(String appId, String fileId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    Form<RfiReplyForm> rfiReplyForm = formFactory.form(RfiReplyForm.class).bindFromRequest();
    String rfiId = rfiReplyForm.data().get("rfiId");
    if (!rfiViewService.isReplyAllowed(userId, rfiId, appData)) {
      LOGGER.error("Unable to delete fileId %s Reply to rfiId {} and appId {} not allowed", fileId, rfiId, appId);
      return showRfiTab(appId);
    } else {
      try {
        draftDao.deleteFile(rfiId, fileId, DraftType.RFI_REPLY);
      } catch (DatabaseException databaseException) {
        // Since this error could occur if the user refreshes the page, we do not return a bad request.
        LOGGER.warn("Unable to delete file.", databaseException);
      }
      rfiReplyForm.discardErrors();
      return showReplyForm(appId, rfiId, rfiReplyForm);
    }
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public Result submitReply(String appId) {
    String userId = userService.getCurrentUserId();
    Form<RfiReplyForm> rfiReplyForm = formFactory.form(RfiReplyForm.class).bindFromRequest();
    String rfiId = rfiReplyForm.data().get("rfiId");
    List<UploadFile> uploadFiles = FileUtil.getUploadFiles(request());
    FileUtil.processErrors(rfiReplyForm, uploadFiles);
    AppData appData = appDataService.getAppData(appId);
    if (!rfiViewService.isReplyAllowed(userId, rfiId, appData)) {
      LOGGER.error("Reply to rfiId {} and appId {} not allowed", rfiId, appId);
      return showRfiTab(appId);
    } else if (rfiReplyForm.hasErrors()) {
      return showReplyForm(appId, rfiId, rfiReplyForm);
    } else {
      String message = rfiReplyForm.get().replyMessage;
      rfiReplyService.insertRfiReply(userId, appId, rfiId, message, uploadFiles);
      flash("success", "Your message has been sent.");
      return redirect(controllers.routes.RfiTabController.showRfiTab(appId));
    }
  }

  public Result showReplyForm(String appId, String rfiId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    if (!rfiViewService.isReplyAllowed(userId, rfiId, appData)) {
      LOGGER.error("Reply to rfiId {} and appId {} not allowed", rfiId, appId);
      return showRfiTab(appId);
    } else {
      RfiReplyForm rfiReplyForm = new RfiReplyForm();
      rfiReplyForm.rfiId = rfiId;
      Form<RfiReplyForm> form = formFactory.form(RfiReplyForm.class).fill(rfiReplyForm);
      return showReplyForm(appId, rfiId, form);
    }
  }

  public Result showRfiTab(String appId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    ReadData readData = readDataService.getReadData(userId, appData);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appData);
    ApplicationTabsView applicationTabsView = applicationTabsViewService.getApplicationTabsView(appData, readData);
    List<RfiView> rfiViews = rfiViewService.getRfiViews(userId, appData);
    readDataService.updateRfiTabReadData(userId, appData, readData);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, applicationTabsView, rfiViews, null, null)).withHeader("Cache-Control", "no-store");
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
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, applicationTabsView, rfiViews, rfiReplyForm, addRfiReplyView)).withHeader("Cache-Control", "no-store");
  }

}

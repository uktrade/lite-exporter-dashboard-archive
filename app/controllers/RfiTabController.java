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
import components.service.UserService;
import components.upload.UploadFile;
import components.upload.UploadMultipartParser;
import components.util.ApplicationUtil;
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
import views.html.rfiListTab;

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
                          ReadDataService readDataService) {
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
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public Result deleteFileById(String appId, String fileId) {
    Form<RfiReplyForm> rfiReplyForm = formFactory.form(RfiReplyForm.class).bindFromRequest();
    String rfiId = rfiReplyForm.data().get("rfiId");
    try {
      draftDao.deleteFile(rfiId, fileId, DraftType.RFI_REPLY);
    } catch (DatabaseException databaseException) {
      // Since this error could occur if the user refreshes the page, we do not return a bad request.
      LOGGER.warn("Unable to delete file.", databaseException);
    }
    rfiReplyForm.discardErrors();
    return showReplyForm(appId, rfiId, rfiReplyForm);
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public Result submitReply(String appId) {
    Form<RfiReplyForm> rfiReplyForm = formFactory.form(RfiReplyForm.class).bindFromRequest();
    String rfiId = rfiReplyForm.data().get("rfiId");
    List<UploadFile> uploadFiles = FileUtil.getUploadFiles(request());
    FileUtil.processErrors(rfiReplyForm, uploadFiles);
    AppData appData = appDataService.getAppData(appId);
    if (alreadyHasReply(rfiId)) {
      LOGGER.error("Reply to rfiId {} and appId {} not possible since a reply already exists", rfiId, appId);
      return showRfiTab(appId);
    } else if (!allowReplies(appData)) {
      LOGGER.error("Reply to rfiId {} and appId {} not possible since application is complete.", rfiId, appId);
      return showRfiTab(appId);
    } else if (rfiReplyForm.hasErrors()) {
      return showReplyForm(appId, rfiId, rfiReplyForm);
    } else {
      String userId = userService.getCurrentUserId();
      String message = rfiReplyForm.get().replyMessage;
      rfiReplyService.insertRfiReply(userId, appId, rfiId, message, uploadFiles);
      flash("success", "Your message has been sent.");
      return redirect(controllers.routes.RfiTabController.showRfiTab(appId));
    }
  }

  public Result showReplyForm(String appId, String rfiId) {
    AppData appData = appDataService.getAppData(appId);
    if (alreadyHasReply(rfiId)) {
      LOGGER.error("Reply to rfiId {} and appId {} not possible since a reply already exists", rfiId);
      flash(rfiId, "You have already submitted your reply to this RFI - you cannot edit or re-submit it.");
      return redirect(routes.RfiTabController.showRfiTab(appId).withFragment(rfiId));
    } else if (!allowReplies(appData)) {
      LOGGER.error("Reply to rfiId {} and appId {} not possible since application is complete.", rfiId, appId);
      return showRfiTab(appId);
    } else {
      RfiReplyForm rfiReplyForm = new RfiReplyForm();
      rfiReplyForm.rfiId = rfiId;
      Form<RfiReplyForm> form = formFactory.form(RfiReplyForm.class).fill(rfiReplyForm);
      return showReplyForm(appId, rfiId, form);
    }
  }

  private Result showReplyForm(String appId, String rfiId, Form<RfiReplyForm> rfiReplyForm) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    ReadData readData = readDataService.getReadData(userId, appData);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appData);
    ApplicationTabsView applicationTabsView = applicationTabsViewService.getApplicationTabsView(appData, readData);
    List<RfiView> rfiViews = rfiViewService.getRfiViews(appData);
    AddRfiReplyView addRfiReplyView = rfiViewService.getAddRfiReplyView(appId, rfiId);
    readDataService.updateRfiTabReadData(userId, appData, readData);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, applicationTabsView, rfiViews, allowReplies(appData), rfiReplyForm, addRfiReplyView)).withHeader("Cache-Control", "no-store");
  }

  public Result showRfiTab(String appId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    ReadData readData = readDataService.getReadData(userId, appData);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appData);
    ApplicationTabsView applicationTabsView = applicationTabsViewService.getApplicationTabsView(appData, readData);
    List<RfiView> rfiViews = rfiViewService.getRfiViews(appData);
    readDataService.updateRfiTabReadData(userId, appData, readData);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, applicationTabsView, rfiViews, allowReplies(appData), null, null)).withHeader("Cache-Control", "no-store");
  }

  private boolean alreadyHasReply(String rfiId) {
    return rfiReplyDao.getRfiReply(rfiId) != null;
  }

  private boolean allowReplies(AppData appData) {
    return ApplicationUtil.isApplicationInProgress(appData);
  }

}

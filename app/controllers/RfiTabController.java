package controllers;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.dao.RfiResponseDao;
import components.exceptions.DatabaseException;
import components.service.ApplicationService;
import components.service.ApplicationSummaryViewService;
import components.service.RfiResponseService;
import components.service.RfiViewService;
import components.upload.UploadFile;
import components.upload.UploadMultipartParser;
import components.util.FileUtil;
import models.enums.DraftType;
import models.view.AddRfiResponseView;
import models.view.ApplicationSummaryView;
import models.view.RfiView;
import models.view.form.RfiResponseForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.rfiListTab;

import java.util.List;

public class RfiTabController extends Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(RfiTabController.class);

  private final String licenceApplicationAddress;
  private final FormFactory formFactory;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final RfiViewService rfiViewService;
  private final RfiResponseDao rfiResponseDao;
  private final ApplicationService applicationService;
  private final RfiResponseService rfiResponseService;
  private final DraftDao draftDao;

  @Inject
  public RfiTabController(String licenceApplicationAddress,
                          FormFactory formFactory,
                          ApplicationSummaryViewService applicationSummaryViewService,
                          RfiViewService rfiViewService,
                          RfiResponseDao rfiResponseDao,
                          ApplicationService applicationService,
                          RfiResponseService rfiResponseService,
                          DraftDao draftDao) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.formFactory = formFactory;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.rfiViewService = rfiViewService;
    this.rfiResponseDao = rfiResponseDao;
    this.applicationService = applicationService;
    this.rfiResponseService = rfiResponseService;
    this.draftDao = draftDao;
  }

  @BodyParser.Of(UploadMultipartParser.class)
  public Result deleteFileById(String appId, String fileId) {
    Form<RfiResponseForm> rfiResponseForm = formFactory.form(RfiResponseForm.class).bindFromRequest();
    String rfiId = rfiResponseForm.data().get("rfiId");
    try {
      draftDao.deleteFile(rfiId, fileId, DraftType.RFI_RESPONSE);
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
    String rfiId = rfiResponseForm.data().get("rfiId");

    List<UploadFile> uploadFiles = FileUtil.getUploadFiles(request());
    FileUtil.processErrors(rfiResponseForm, uploadFiles);

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
      rfiResponseService.insertRfiResponse(rfiId, responseMessage, uploadFiles);
      flash("success", "Your message has been sent.");
      return redirect(controllers.routes.RfiTabController.showRfiTab(appId));
    }
  }

  public Result showResponseForm(String appId, String rfiId) {
    if (alreadyHasResponse(rfiId)) {
      LOGGER.error("Response to rfiId {} and appId {} not possible since a response already exists", rfiId);
      flash(rfiId, "You have already submitted your reply to this RFI - you cannot edit or re-submit it.");
      return redirect(routes.RfiTabController.showRfiTab(appId).withFragment(rfiId));
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
    AddRfiResponseView addRfiResponseView = rfiViewService.getAddRfiResponseView(appId, rfiId);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, rfiViews, allowResponses(appId), rfiResponseForm, addRfiResponseView)).withHeader("Cache-Control", "no-store");
  }

  public Result showRfiTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    List<RfiView> rfiViews = rfiViewService.getRfiViews(appId);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, rfiViews, allowResponses(appId), null, null)).withHeader("Cache-Control", "no-store");
  }

  private boolean alreadyHasResponse(String rfiId) {
    return rfiResponseDao.getRfiResponse(rfiId) != null;
  }

  private boolean allowResponses(String appId) {
    return applicationService.isApplicationInProgress(appId);
  }

}

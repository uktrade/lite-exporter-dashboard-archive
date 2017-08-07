package controllers;

import com.google.inject.Inject;
import components.dao.RfiResponseDao;
import components.service.RfiViewService;
import components.service.StatusItemViewService;
import components.service.UserService;
import models.RfiResponse;
import models.view.RfiResponseForm;
import models.view.RfiView;
import models.view.StatusItemView;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.licenceApplication;

import java.time.Instant;
import java.util.List;

public class LicenseApplicationController extends Controller {

  private final StatusItemViewService statusItemViewService;
  private final RfiViewService rfiViewService;
  private final UserService userService;
  private final FormFactory formFactory;
  private final RfiResponseDao rfiResponseDao;

  @Inject
  public LicenseApplicationController(StatusItemViewService statusItemViewService,
                                      RfiViewService rfiViewService,
                                      UserService userService,
                                      FormFactory formFactory, RfiResponseDao rfiResponseDao) {
    this.statusItemViewService = statusItemViewService;
    this.rfiViewService = rfiViewService;
    this.userService = userService;
    this.formFactory = formFactory;
    this.rfiResponseDao = rfiResponseDao;
  }

  public Result submitReply(String appId, String rfiId) {
    Form<RfiResponseForm> rfiResponseForm = formFactory.form(RfiResponseForm.class).bindFromRequest();
    if (rfiResponseForm.hasErrors()) {
      return reply(appId, rfiId, rfiResponseForm);
    } else {
      String responseMessage = rfiResponseForm.get().responseMessage;
      String sentBy = userService.getCurrentUser();
      RfiResponse rfiResponse = new RfiResponse(rfiId, sentBy, Instant.now().toEpochMilli(), responseMessage, null);
      rfiResponseDao.insertRfiResponse(rfiResponse);
      return redirect(routes.LicenseApplicationController.rfiTab(appId).withFragment(rfiId));
    }
  }

  public Result reply(String appId, String rfiId) {
    Form<RfiResponseForm> form = formFactory.form(RfiResponseForm.class);
    return reply(appId, rfiId, form);
  }

  private Result reply(String appId, String rfiId, Form<RfiResponseForm> rfiResponseForm) {
    List<RfiView> rfiViews = rfiViewService.getRfiViewsWithReply(appId, rfiId);
    return ok(licenceApplication.render(appId, "rfi", null, rfiViews.size(), rfiViews, rfiResponseForm));
  }

  public Result statusTab(String appId) {
    List<StatusItemView> statusItemViewList = statusItemViewService.getStatusItemViews(appId);
    return ok(licenceApplication.render(appId, "statusTracker", statusItemViewList, rfiViewCount(appId), null, null));
  }

  public Result rfiTab(String appId) {
    List<RfiView> rfiViews = rfiViewService.getRfiViews(appId);
    return ok(licenceApplication.render(appId, "rfi", null, rfiViews.size(), rfiViews, null));
  }

  public Result amendTab(String appId) {
    return ok(licenceApplication.render(appId, "amend", null, rfiViewCount(appId), null, null));
  }

  public Result outcomeTab(String appId) {
    return ok(licenceApplication.render(appId, "outcomeDocs", null, rfiViewCount(appId), null, null));
  }

  private int rfiViewCount(String appId) {
    return rfiViewService.getRfiViewCount(appId);
  }

}

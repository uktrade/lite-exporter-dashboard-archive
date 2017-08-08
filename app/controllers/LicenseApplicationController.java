package controllers;

import com.google.inject.Inject;
import components.dao.RfiResponseDao;
import components.service.RfiViewService;
import components.service.StatusItemViewService;
import components.service.UserService;
import models.RfiResponse;
import models.view.AddRfiResponseView;
import models.view.RfiResponseForm;
import models.view.RfiView;
import models.view.StatusItemView;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.licenceApplicationTabs.amendApplicationTab;
import views.html.licenceApplicationTabs.outcomeDocsTab;
import views.html.licenceApplicationTabs.rfiListTab;
import views.html.licenceApplicationTabs.statusTrackerTab;

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
                                      FormFactory formFactory,
                                      RfiResponseDao rfiResponseDao) {
    this.statusItemViewService = statusItemViewService;
    this.rfiViewService = rfiViewService;
    this.userService = userService;
    this.formFactory = formFactory;
    this.rfiResponseDao = rfiResponseDao;
  }

  public Result submitReply(String appId) {
    Form<RfiResponseForm> rfiResponseForm = formFactory.form(RfiResponseForm.class).bindFromRequest();
    String rfiId = rfiResponseForm.data().get("rfiId");
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
    RfiResponseForm rfiResponseForm = new RfiResponseForm();
    rfiResponseForm.rfiId = rfiId;
    Form<RfiResponseForm> form = formFactory.form(RfiResponseForm.class).fill(rfiResponseForm);
    return reply(appId, rfiId, form);
  }

  private Result reply(String appId, String rfiId, Form<RfiResponseForm> rfiResponseForm) {
    List<RfiView> rfiViews = rfiViewService.getRfiViews(appId);
    AddRfiResponseView addRfiResponseView = rfiViewService.getAddRfiResponseView(rfiId);
    return ok(rfiListTab.render(appId, rfiViews, rfiResponseForm, addRfiResponseView));
  }

  public Result rfiTab(String appId) {
    List<RfiView> rfiViews = rfiViewService.getRfiViews(appId);
    return ok(rfiListTab.render(appId, rfiViews, null, null));
  }

  public Result statusTab(String appId) {
    List<StatusItemView> statusItemViewList = statusItemViewService.getStatusItemViews(appId);
    return ok(statusTrackerTab.render(appId, rfiViewCount(appId), statusItemViewList));
  }

  public Result amendTab(String appId) {
    return ok(amendApplicationTab.render(appId, rfiViewCount(appId)));
  }

  public Result outcomeTab(String appId) {
    return ok(outcomeDocsTab.render(appId, rfiViewCount(appId)));
  }

  private int rfiViewCount(String appId) {
    return rfiViewService.getRfiViewCount(appId);
  }

}

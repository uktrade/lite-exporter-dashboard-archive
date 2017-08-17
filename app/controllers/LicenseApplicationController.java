package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.dao.AmendmentDao;
import components.dao.RfiResponseDao;
import components.dao.WithdrawalRequestDao;
import components.service.ApplicationSummaryViewService;
import components.service.RfiViewService;
import components.service.StatusItemViewService;
import components.service.UserService;
import components.util.RandomUtil;
import models.Amendment;
import models.RfiResponse;
import models.WithdrawalRequest;
import models.view.AddRfiResponseView;
import models.view.ApplicationSummaryView;
import models.view.RfiView;
import models.view.StatusItemView;
import models.view.form.AmendApplicationForm;
import models.view.form.RfiResponseForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import views.html.licenceApplicationTabs.amendApplicationTab;
import views.html.licenceApplicationTabs.outcomeDocsTab;
import views.html.licenceApplicationTabs.rfiListTab;
import views.html.licenceApplicationTabs.statusTrackerTab;

import java.time.Instant;
import java.util.List;

public class LicenseApplicationController extends Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(LicenseApplicationController.class);

  private final StatusItemViewService statusItemViewService;
  private final RfiViewService rfiViewService;
  private final UserService userService;
  private final FormFactory formFactory;
  private final RfiResponseDao rfiResponseDao;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final String licenceApplicationAddress;
  private final AmendmentDao amendmentDao;
  private final WithdrawalRequestDao withdrawalRequestDao;

  @Inject
  public LicenseApplicationController(StatusItemViewService statusItemViewService,
                                      RfiViewService rfiViewService,
                                      UserService userService,
                                      FormFactory formFactory,
                                      RfiResponseDao rfiResponseDao, ApplicationSummaryViewService applicationSummaryViewService,
                                      @Named("licenceApplicationAddress") String licenceApplicationAddress,
                                      AmendmentDao amendmentDao,
                                      WithdrawalRequestDao withdrawalRequestDao) {
    this.statusItemViewService = statusItemViewService;
    this.rfiViewService = rfiViewService;
    this.userService = userService;
    this.formFactory = formFactory;
    this.rfiResponseDao = rfiResponseDao;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.amendmentDao = amendmentDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
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
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    List<RfiView> rfiViews = rfiViewService.getRfiViews(appId);
    AddRfiResponseView addRfiResponseView = rfiViewService.getAddRfiResponseView(rfiId);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, rfiViews, rfiResponseForm, addRfiResponseView));
  }

  public Result rfiTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    List<RfiView> rfiViews = rfiViewService.getRfiViews(appId);
    return ok(rfiListTab.render(licenceApplicationAddress, applicationSummaryView, rfiViews, null, null));
  }

  public Result statusTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    List<StatusItemView> statusItemViewList = statusItemViewService.getStatusItemViews(appId);
    return ok(statusTrackerTab.render(licenceApplicationAddress, applicationSummaryView, getRfiViewCount(appId), statusItemViewList));
  }

  public Result amendApplication(String appId) {
    Form<AmendApplicationForm> amendApplicationForm = formFactory.form(AmendApplicationForm.class).bindFromRequest();
    String action = amendApplicationForm.data().get("action");
    if (amendApplicationForm.hasErrors()) {
      return amendTab(appId, Option.apply(action));
    } else {
      String message = amendApplicationForm.get().message;
      if ("amend".equals(action)) {
        Amendment amendment = new Amendment(RandomUtil.random("AME"), appId, Instant.now().toEpochMilli(), userService.getCurrentUser(), message, null);
        amendmentDao.insertAmendment(amendment);
      } else if ("withdraw".equals(action)) {
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest(RandomUtil.random("WIT"),
            appId,
            Instant.now().toEpochMilli(),
            userService.getCurrentUser(),
            message,
            null,
            null,
            null,
            null);
        withdrawalRequestDao.insertWithdrawalRequest(withdrawalRequest);
      }
      LOGGER.error(action);
      LOGGER.error(message);
      return amendTab(appId, Option.apply(null));
    }
  }

  public Result amendTab(String appId, Option<String> action) {
    String actionStr = null;
    if (action.isDefined() && ("amend".equals(action.get()) || "withdraw".equals(action.get()))) {
      actionStr = action.get();
    }
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    AmendApplicationForm amendApplicationForm = new AmendApplicationForm();
    amendApplicationForm.action = actionStr;
    Form<AmendApplicationForm> form = formFactory.form(AmendApplicationForm.class).fill(amendApplicationForm);
    return ok(amendApplicationTab.render(licenceApplicationAddress, applicationSummaryView, getRfiViewCount(appId), actionStr, form));
  }

  public Result outcomeTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    return ok(outcomeDocsTab.render(licenceApplicationAddress, applicationSummaryView, getRfiViewCount(appId)));
  }

  private int getRfiViewCount(String appId) {
    return rfiViewService.getRfiViewCount(appId);
  }

}

package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.dao.AmendmentDao;
import components.dao.WithdrawalRequestDao;
import components.service.ApplicationSummaryViewService;
import components.service.OfficerViewService;
import components.service.RfiViewService;
import components.service.StatusItemViewService;
import components.service.UserService;
import components.util.EnumUtil;
import components.util.RandomUtil;
import models.Amendment;
import models.User;
import models.WithdrawalRequest;
import models.enums.Action;
import models.view.ApplicationSummaryView;
import models.view.OfficerView;
import models.view.StatusItemView;
import models.view.form.AmendApplicationForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import views.html.licenceApplicationTabs.amendApplicationTab;
import views.html.licenceApplicationTabs.outcomeDocsTab;
import views.html.licenceApplicationTabs.statusTrackerTab;

import java.time.Instant;
import java.util.List;

public class ApplicationDetailsController extends Controller {

  private final String licenceApplicationAddress;
  private final StatusItemViewService statusItemViewService;
  private final RfiViewService rfiViewService;
  private final UserService userService;
  private final FormFactory formFactory;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final AmendmentDao amendmentDao;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final OfficerViewService officerViewService;

  @Inject
  public ApplicationDetailsController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                                      StatusItemViewService statusItemViewService,
                                      RfiViewService rfiViewService,
                                      UserService userService,
                                      FormFactory formFactory,
                                      ApplicationSummaryViewService applicationSummaryViewService,
                                      AmendmentDao amendmentDao,
                                      WithdrawalRequestDao withdrawalRequestDao,
                                      OfficerViewService officerViewService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.statusItemViewService = statusItemViewService;
    this.rfiViewService = rfiViewService;
    this.userService = userService;
    this.formFactory = formFactory;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.amendmentDao = amendmentDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.officerViewService = officerViewService;
  }

  public Result statusTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    List<StatusItemView> statusItemViewList = statusItemViewService.getStatusItemViews(appId);
    return ok(statusTrackerTab.render(licenceApplicationAddress, applicationSummaryView, getRfiViewCount(appId), statusItemViewList));
  }

  public Result amendApplication(String appId) {
    User currentUser = userService.getCurrentUser();
    Form<AmendApplicationForm> amendApplicationForm = formFactory.form(AmendApplicationForm.class).bindFromRequest();
    Action action = EnumUtil.parse(amendApplicationForm.data().get("action"), Action.class);
    if (amendApplicationForm.hasErrors()) {
      return amendTab(appId, Option.apply(action.toString()));
    } else {
      String message = amendApplicationForm.get().message;
      if (action == Action.AMEND) {
        Amendment amendment = new Amendment(RandomUtil.random("AME"), appId, Instant.now().toEpochMilli(), currentUser.getId(), message, null);
        amendmentDao.insertAmendment(amendment);
      } else if (action == Action.WITHDRAW) {
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest(RandomUtil.random("WIT"),
            appId,
            Instant.now().toEpochMilli(),
            currentUser.getId(),
            message,
            null,
            null,
            null,
            null);
        withdrawalRequestDao.insertWithdrawalRequest(withdrawalRequest);
      }
      return amendTab(appId, Option.apply(null));
    }
  }

  public Result amendTab(String appId, Option<String> actionOption) {
    Action action = EnumUtil.parse(parse(actionOption), Action.class);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    AmendApplicationForm amendApplicationForm = new AmendApplicationForm();
    if (action != null) {
      amendApplicationForm.action = action.toString();
    }
    Form<AmendApplicationForm> form = formFactory.form(AmendApplicationForm.class).fill(amendApplicationForm);
    OfficerView officerView = officerViewService.getOfficerView(appId);
    return ok(amendApplicationTab.render(licenceApplicationAddress, applicationSummaryView, getRfiViewCount(appId), action, form, officerView));
  }

  public Result outcomeTab(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    return ok(outcomeDocsTab.render(licenceApplicationAddress, applicationSummaryView, getRfiViewCount(appId)));
  }

  private int getRfiViewCount(String appId) {
    return rfiViewService.getRfiViewCount(appId);
  }

  private String parse(Option<String> str) {
    return str.isDefined() ? str.get() : null;
  }

}

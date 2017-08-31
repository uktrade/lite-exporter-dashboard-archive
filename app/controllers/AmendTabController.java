package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.dao.AmendmentDao;
import components.dao.StatusUpdateDao;
import components.dao.WithdrawalRequestDao;
import components.service.ApplicationSummaryViewService;
import components.service.OfficerViewService;
import components.service.RfiViewService;
import components.service.UserService;
import components.util.EnumUtil;
import components.util.RandomUtil;
import models.Amendment;
import models.User;
import models.WithdrawalRequest;
import models.enums.Action;
import models.enums.StatusType;
import models.view.ApplicationSummaryView;
import models.view.OfficerView;
import models.view.form.AmendApplicationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.licenceApplicationTabs.amendApplicationTab;

import java.time.Instant;

public class AmendTabController extends Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmendTabController.class);

  private final String licenceApplicationAddress;
  private final UserService userService;
  private final FormFactory formFactory;
  private final AmendmentDao amendmentDao;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final OfficerViewService officerViewService;
  private final RfiViewService rfiViewService;
  private final StatusUpdateDao statusUpdateDao;

  @Inject
  public AmendTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                            UserService userService,
                            FormFactory formFactory,
                            AmendmentDao amendmentDao,
                            WithdrawalRequestDao withdrawalRequestDao,
                            ApplicationSummaryViewService applicationSummaryViewService,
                            OfficerViewService officerViewService,
                            RfiViewService rfiViewService,
                            StatusUpdateDao statusUpdateDao) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.userService = userService;
    this.formFactory = formFactory;
    this.amendmentDao = amendmentDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.officerViewService = officerViewService;
    this.rfiViewService = rfiViewService;
    this.statusUpdateDao = statusUpdateDao;
  }

  public Result amendApplication(String appId) {
    Form<AmendApplicationForm> amendApplicationForm = formFactory.form(AmendApplicationForm.class).bindFromRequest();
    String actionParam = amendApplicationForm.data().get("action");
    Action action = EnumUtil.parse(actionParam, Action.class);
    if (action == null) {
      LOGGER.error("Amending application with action {} not possible", actionParam);
      return showAmendTab(appId, null);
    } else if (!allowAmendment(appId)) {
      LOGGER.error("Amending application with appId {} and action {} not possible since application is complete.", appId, action);
      return showAmendTab(appId, null);
    } else if (amendApplicationForm.hasErrors()) {
      return showAmendTab(appId, action.toString());
    } else {
      String message = amendApplicationForm.get().message;
      if (action == Action.AMEND) {
        insertAmendment(appId, message);
      } else if (action == Action.WITHDRAW) {
        insertWithdrawalRequest(appId, message);
      }
      return showAmendTab(appId, null);
    }
  }

  private void insertAmendment(String appId, String message) {
    User currentUser = userService.getCurrentUser();
    Amendment amendment = new Amendment(RandomUtil.random("AME"), appId, Instant.now().toEpochMilli(), currentUser.getId(), message, null);
    amendmentDao.insertAmendment(amendment);
  }

  private void insertWithdrawalRequest(String appId, String message) {
    User currentUser = userService.getCurrentUser();
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

  public Result showAmendTab(String appId, String actionParam) {
    boolean allowAmendment = allowAmendment(appId);
    Action action = EnumUtil.parse(actionParam, Action.class);
    if (!allowAmendment && action != null) {
      LOGGER.error("Amending application with appId {} and action {} not possible since application is complete.", appId, actionParam);
      return showAmendTab(appId, null);
    } else {
      ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
      AmendApplicationForm amendApplicationForm = new AmendApplicationForm();
      if (action != null) {
        amendApplicationForm.action = action.toString();
      }
      Form<AmendApplicationForm> form = formFactory.form(AmendApplicationForm.class).fill(amendApplicationForm);
      OfficerView officerView = officerViewService.getOfficerView(appId);
      int rfiViewCount = rfiViewService.getRfiViewCount(appId);
      return ok(amendApplicationTab.render(licenceApplicationAddress, applicationSummaryView, rfiViewCount, allowAmendment, action, form, officerView));
    }
  }

  private boolean allowAmendment(String appId) {
    return statusUpdateDao.getStatusUpdates(appId).stream()
        .noneMatch(statusUpdate -> statusUpdate.getStatusType() == StatusType.COMPLETE);
  }

}

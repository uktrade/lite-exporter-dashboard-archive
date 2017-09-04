package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.service.AmendmentService;
import components.service.ApplicationService;
import components.service.ApplicationSummaryViewService;
import components.service.OfficerViewService;
import components.service.RfiViewService;
import components.service.WithdrawalRequestService;
import components.util.EnumUtil;
import models.enums.Action;
import models.view.ApplicationSummaryView;
import models.view.OfficerView;
import models.view.form.AmendApplicationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.amendApplicationTab;

public class AmendTabController extends Controller {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmendTabController.class);

  private final String licenceApplicationAddress;
  private final FormFactory formFactory;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final OfficerViewService officerViewService;
  private final RfiViewService rfiViewService;
  private final ApplicationService applicationService;
  private final AmendmentService amendmentService;
  private final WithdrawalRequestService withdrawalRequestService;

  @Inject
  public AmendTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                            FormFactory formFactory,
                            ApplicationSummaryViewService applicationSummaryViewService,
                            OfficerViewService officerViewService,
                            RfiViewService rfiViewService,
                            ApplicationService applicationService,
                            AmendmentService amendmentService,
                            WithdrawalRequestService withdrawalRequestService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.formFactory = formFactory;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.officerViewService = officerViewService;
    this.rfiViewService = rfiViewService;
    this.applicationService = applicationService;
    this.amendmentService = amendmentService;
    this.withdrawalRequestService = withdrawalRequestService;
  }

  public Result submitAmendment(String appId) {
    Form<AmendApplicationForm> amendApplicationForm = formFactory.form(AmendApplicationForm.class).bindFromRequest();
    String actionParam = amendApplicationForm.data().get("action");
    Action action = EnumUtil.parse(actionParam, Action.class);
    if (action == null) {
      LOGGER.error("Amending application with action {} not possible", actionParam);
      return showAmendTab(appId, null, null);
    } else if (!allowAmendment(appId)) {
      LOGGER.error("Amending application with appId {} and action {} not possible since application is complete.", appId, action);
      return showAmendTab(appId, null, null);
    } else if (amendApplicationForm.hasErrors()) {
      return showAmendTab(appId, action, amendApplicationForm);
    } else {
      String message = amendApplicationForm.get().message;
      if (action == Action.AMEND) {
        amendmentService.insertAmendment(appId, message);
      } else if (action == Action.WITHDRAW) {
        withdrawalRequestService.insertWithdrawalRequest(appId, message);
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
    return ok(amendApplicationTab.render(licenceApplicationAddress, applicationSummaryView, rfiViewCount, allowAmendment(appId), action, form, officerView));
  }

  private boolean allowAmendment(String appId) {
    return applicationService.isApplicationInProgress(appId);
  }

}

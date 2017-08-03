package controllers;

import com.google.inject.Inject;
import components.dao.StatusUpdateDao;
import components.service.StatusItemViewService;
import models.StatusUpdate;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.licenceApplication;

import java.util.List;

public class LicenseApplicationController extends Controller {

  private final StatusItemViewService statusItemViewService;
  private final StatusUpdateDao statusUpdateDao;

  @Inject
  public LicenseApplicationController(StatusItemViewService statusItemViewService, StatusUpdateDao statusUpdateDao) {
    this.statusItemViewService = statusItemViewService;
    this.statusUpdateDao = statusUpdateDao;
  }

  public Result licenceApplication(String applicationRef, String activeTab) {
    List<StatusUpdate> statusUpdates = statusUpdateDao.getStatusUpdates(applicationRef);
    return ok(licenceApplication.render(applicationRef, activeTab, statusItemViewService.getStatusItemViews(statusUpdates)));
  }


}

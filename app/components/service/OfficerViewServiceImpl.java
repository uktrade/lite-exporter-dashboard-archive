package components.service;

import com.google.inject.Inject;
import components.dao.ApplicationDao;
import models.Application;
import models.User;
import models.view.OfficerView;

public class OfficerViewServiceImpl implements OfficerViewService {

  private final ApplicationDao applicationDao;
  private final UserService userService;

  @Inject
  public OfficerViewServiceImpl(ApplicationDao applicationDao, UserService userService) {
    this.applicationDao = applicationDao;
    this.userService = userService;
  }

  @Override
  public OfficerView getOfficerView(String appId) {
    Application application = applicationDao.getApplication(appId);
    if (application.getCaseOfficerId() != null) {
      User user = userService.getUser(application.getCaseOfficerId());
      return new OfficerView(user.getFirstName() + " " + user.getLastName(), user.getTelephone(), user.getEmail());
    } else {
      return null;
    }
  }

}

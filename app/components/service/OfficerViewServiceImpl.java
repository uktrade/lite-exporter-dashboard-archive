package components.service;

import com.google.inject.Inject;
import models.User;
import models.view.OfficerView;

public class OfficerViewServiceImpl implements OfficerViewService {

  private final UserService userService;

  @Inject
  public OfficerViewServiceImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public OfficerView getOfficerView(String officerId) {
    if (officerId != null) {
      User user = userService.getUser(officerId);
      return new OfficerView(user.getFirstName() + " " + user.getLastName(), user.getTelephone(), user.getEmail());
    } else {
      return null;
    }
  }

}

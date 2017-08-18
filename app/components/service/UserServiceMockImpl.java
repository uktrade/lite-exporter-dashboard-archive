package components.service;

import components.exceptions.ServiceException;
import models.User;

import java.util.HashMap;
import java.util.Map;

public class UserServiceMockImpl implements UserService {

  private static final Map<String, User> users = new HashMap<>();

  static {
    users.put("24492", new User("24492", "Kathryn Smith", null, null));
    users.put("2", new User("2", "Christoph", null, null));
    users.put("3", new User("3", "Jerry McGuire", "j.mcguire@trade.gov.uk", "01234 567890"));
  }

  @Override
  public User getUser(String userId) {
    User user = users.get(userId);
    if (user != null) {
      return user;
    } else {
      throw new ServiceException("Unknown user: " + userId);
    }
  }

  // TODO: This will later be removed and come from the session
  @Override
  public User getCurrentUser() {
    return users.get("24492");
  }

}

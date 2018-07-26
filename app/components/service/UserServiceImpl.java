package components.service;

import com.google.inject.Inject;
import components.common.auth.AuthInfo;
import components.common.auth.SpireAuthManager;
import components.exceptions.ServiceException;
import models.User;
import org.apache.commons.lang3.StringUtils;

public class UserServiceImpl implements UserService {

  private final SpireAuthManager spireAuthManager;

  @Inject
  public UserServiceImpl(SpireAuthManager spireAuthManager) {
    this.spireAuthManager = spireAuthManager;
  }

  @Override
  public String getUsername(String userId) {
    User user = getUser(userId);
    return user.getFirstName() + " " + user.getLastName();
  }

  @Override
  public User getUser(String userId) {
    if (StringUtils.isBlank(userId)) {
      throw new ServiceException("UserId is blank. Unable to get user.");
    } else {
      AuthInfo authInfo = spireAuthManager.getAuthInfoFromContext();
      if (!authInfo.isAuthenticated()) {
        throw new ServiceException("No user logged in. Unable to get user with userId " + userId);
      } else if (userId.equals(authInfo.getId())) {
        return new User(authInfo.getId(), authInfo.getForename(), authInfo.getSurname(), authInfo.getEmail(), null);
      } else {
        throw new ServiceException("UserId different from logged in user. Unable to get user with userId " + userId);
      }
    }
  }

  @Override
  public String getCurrentUserId() {
    AuthInfo authInfo = spireAuthManager.getAuthInfoFromContext();
    if (!authInfo.isAuthenticated()) {
      throw new ServiceException("No user logged in. Unable to get current userId.");
    } else {
      return authInfo.getId();
    }
  }

}

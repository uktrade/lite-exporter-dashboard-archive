package components.service.test;

import com.google.inject.Inject;
import components.common.auth.AuthInfo;
import components.common.auth.SpireAuthManager;
import components.exceptions.ServiceException;
import components.service.UserPrivilegeService;
import components.service.UserService;
import java.util.HashMap;
import java.util.Map;
import models.User;
import org.apache.commons.lang3.StringUtils;

public class TestUserServiceImpl implements UserService {

  private static final Map<String, User> users = new HashMap<>();
  private final SpireAuthManager spireAuthManager;
  private final UserPrivilegeService userPrivilegeService;

  @Inject
  public TestUserServiceImpl(SpireAuthManager spireAuthManager, UserPrivilegeService userPrivilegeService) {
    this.spireAuthManager = spireAuthManager;
    this.userPrivilegeService = userPrivilegeService;
  }

  static {
    users.put(TestDataServiceImpl.OTHER_APPLICANT_ID, new User("2", "Christoph", "Mueller", null, null));
    users.put(TestDataServiceImpl.OFFICER_ID, new User("3", "Jerry", "McGuire", "j.mcguire@trade.gov.uk", "01234 567890"));
  }

  @Override
  public String getUsername(String userId) {
    User user = getUser(userId);
    return user.getFirstName() + " " + user.getLastName();
  }

  @Override
  public User getUser(String userId) {
    if (StringUtils.isBlank(userId)) {
      throw new ServiceException("Unknown user: " + userId);
    } else if (userId.equals(getCurrentUserId())) {
      AuthInfo authInfo = spireAuthManager.getAuthInfoFromContext();
      return new User(authInfo.getId(), authInfo.getForename(), authInfo.getSurname(), authInfo.getEmail(), null);
    } else {
      User user = users.get(userId);
      if (user != null) {
        return user;
      } else {
        throw new ServiceException("Unknown user: " + userId);
      }
    }
  }

  @Override
  public String getCurrentUserId() {
    AuthInfo authInfo = spireAuthManager.getAuthInfoFromContext();
    if (!authInfo.isAuthenticated()) {
      throw new ServiceException("Unable to get current user id since no user is logged in.");
    } else {
      return authInfo.getId();
    }
  }

}

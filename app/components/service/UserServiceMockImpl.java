package components.service;

import models.User;

public class UserServiceMockImpl implements UserService {

  @Override
  public User getCurrentUser() {
    return new User("24492", "Kathryn Smith");
  }

}

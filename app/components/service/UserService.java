package components.service;

import models.User;

public interface UserService {

  User getUser(String userId);

  User getCurrentUser();

}

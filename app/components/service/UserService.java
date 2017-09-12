package components.service;

import models.User;

public interface UserService {

  String getUsername(String userId);

  User getUser(String userId);

  User getCurrentUser();

}

package components.service.test;

public interface TestDataService {

  void deleteAllUsersAndInsertStartData();

  void deleteCurrentUser(String userId);

  void deleteAllData();

}

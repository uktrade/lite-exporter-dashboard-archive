package components.service.test;

public interface TestDataService {

  void deleteAllUsersAndInsertStartData();

  void insertTwoCompanies(String userId);

  void insertOneCompany(String userId);

  void deleteCurrentUser(String userId);

  void deleteAllData();

  void insertUserTestingApplicant(String userId);

  void insertOtherUserApplications(String userId);

}

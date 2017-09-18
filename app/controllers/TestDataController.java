package controllers;

import com.google.inject.Inject;
import components.service.test.TestDataService;
import play.mvc.Result;

public class TestDataController extends SamlController {

  private final TestDataService testDataService;

  @Inject
  public TestDataController(TestDataService testDataService) {
    this.testDataService = testDataService;
  }

  public Result insertDefaultTestData() {
    return insertTestData(null);
  }

  public Result insertTestData(String testType) {
    if ("one".equals(testType)) {
      testDataService.deleteCurrentUserAndInsertOneCompany();
    } else if ("del".equals(testType)) {
      testDataService.deleteCurrentUser();
    } else if ("del-all".equals(testType)) {
      testDataService.deleteAllUsers();
    } else if ("other".equals(testType)) {
      testDataService.deleteCurrentUserAndInsertOtherUserApplications();
    } else {
      testDataService.deleteCurrentUserAndInsertTwoCompanies();
    }
    return redirect(controllers.routes.ApplicationListController.index());
  }

}

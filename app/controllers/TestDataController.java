package controllers;

import com.google.inject.Inject;
import components.service.UserService;
import components.service.test.TestDataService;
import components.util.EnumUtil;
import models.enums.TestType;
import play.mvc.Result;

public class TestDataController extends SamlController {

  private final TestDataService testDataService;
  private final UserService userService;

  @Inject
  public TestDataController(TestDataService testDataService, UserService userService) {
    this.testDataService = testDataService;
    this.userService = userService;
  }

  public Result insertDefaultTestData() {
    return insertTestData(TestType.TWO.toString());
  }

  public Result insertTestData(String testTypeParam) {
    TestType testType = EnumUtil.parse(testTypeParam, TestType.class, TestType.TWO);
    String userId = userService.getCurrentUserId();
    testDataService.deleteCurrentUser(userId);
    switch (testType) {
      case ONE:
        testDataService.insertOneCompany(userId);
        break;
      case TWO:
        testDataService.insertTwoCompanies(userId);
        break;
      case THREE:
        testDataService.insertUserTestingApplicant(userId);
        break;
      case OTHER:
        testDataService.insertOtherUserApplications(userId);
        break;
      case DEL_ALL:
        testDataService.deleteAllData();
        break;
      case RESET_ALL:
        testDataService.deleteAllUsersAndInsertStartData();
        break;
      case DEL:
        // Do nothing since we have already deleted the current user data
        break;
      default:
        throw new RuntimeException("Unknown testType " + testTypeParam);
    }
    return redirect(controllers.routes.ApplicationListController.index());
  }

}

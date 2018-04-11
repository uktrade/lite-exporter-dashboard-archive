package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.exceptions.UnexpectedStateException;
import components.service.UserService;
import components.service.test.TestDataService;
import components.util.EnumUtil;
import models.enums.TestType;
import play.mvc.Result;

public class TestDataController extends SamlController {

  private final TestDataService testDataService;
  private final UserService userService;
  private final boolean test;

  @Inject
  public TestDataController(TestDataService testDataService, UserService userService, @Named("test") boolean test) {
    this.testDataService = testDataService;
    this.userService = userService;
    this.test = test;
  }

  public Result insertDefaultTestData() {
    return insertTestData(TestType.TWO.toString());
  }

  public Result insertTestData(String testTypeParam) {
    TestType testType = EnumUtil.parse(testTypeParam, TestType.class, TestType.TWO);
    String userId = userService.getCurrentUserId();

    if (!test) {
      return redirect(controllers.routes.ApplicationListController.index());
    }

    switch (testType) {
      case ONE:
        testDataService.deleteCurrentUser(userId);
        testDataService.insertOneCompany(userId);
        break;
      case TWO:
        testDataService.deleteCurrentUser(userId);
        testDataService.insertTwoCompanies(userId);
        break;
      case THREE:
        testDataService.deleteCurrentUser(userId);
        testDataService.insertUserTestingApplicant(userId);
        break;
      case OTHER:
        testDataService.deleteCurrentUser(userId);
        testDataService.insertOtherUserApplications(userId);
        break;
      case DEL_ALL:
        testDataService.deleteAllData();
        break;
      case RESET_ALL:
        testDataService.deleteAllUsersAndInsertStartData();
        break;
      case DEL:
        testDataService.deleteCurrentUser(userId);
        break;
      default:
        throw new UnexpectedStateException("Unknown testType " + testTypeParam);
    }
    return redirect(controllers.routes.ApplicationListController.index());
  }

}

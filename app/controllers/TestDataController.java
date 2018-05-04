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

  public Result insertTestData(String testTypeParam) {
    TestType testType = EnumUtil.parse(testTypeParam, TestType.class);
    String userId = userService.getCurrentUserId();

    if (!test || testType == null) {
      return redirect(controllers.routes.IndexController.index());
    }

    switch (testType) {
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
    return redirect(controllers.routes.IndexController.index());
  }

}

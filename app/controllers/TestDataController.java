package controllers;

import com.google.inject.Inject;
import components.service.TestDataService;
import play.mvc.Controller;
import play.mvc.Result;

public class TestDataController extends Controller {

  private final TestDataService testDataService;

  @Inject
  public TestDataController(TestDataService testDataService) {
    this.testDataService = testDataService;
  }

  public Result insertTestData() {
    testDataService.deleteAllDataAndInsertTestData();
    return redirect(routes.Application.index());
  }

}

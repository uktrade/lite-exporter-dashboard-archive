package controllers;

import com.google.inject.Inject;
import components.service.CacheService;
import components.service.TestDataService;
import play.mvc.Controller;
import play.mvc.Result;

public class TestDataController extends Controller {

  private final TestDataService testDataService;

  @Inject
  public TestDataController(TestDataService testDataService, CacheService cacheService) {
    this.testDataService = testDataService;
  }

  public Result insertTestData() {
    session().clear();
    testDataService.deleteAllDataAndInsertTestData();
    return redirect(routes.ApplicationController.index());
  }

}

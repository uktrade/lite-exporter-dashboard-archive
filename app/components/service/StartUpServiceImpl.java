package components.service;

import com.google.inject.Inject;
import components.dao.ApplicationDao;

public class StartUpServiceImpl implements StartUpService {

  private final ApplicationDao applicationDao;
  private final TestDataService testDataService;

  @Inject
  public StartUpServiceImpl(ApplicationDao applicationDao, TestDataService testDataService) {
    this.applicationDao = applicationDao;
    this.testDataService = testDataService;
    startUp();
  }

  private void startUp() {
    long applicationCount = applicationDao.getApplicationCount();
    if (applicationCount == 0) {
      testDataService.deleteAllDataAndInsertTwoCompaniesTestData();
    }
  }

}

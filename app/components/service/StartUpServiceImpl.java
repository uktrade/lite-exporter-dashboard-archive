package components.service;

import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.service.test.TestDataService;
import play.inject.ApplicationLifecycle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StartUpServiceImpl implements StartUpService {

  private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

  private final ApplicationDao applicationDao;
  private final TestDataService testDataService;

  @Inject
  public StartUpServiceImpl(ApplicationLifecycle lifecycle, ApplicationDao applicationDao, TestDataService testDataService) {
    this.applicationDao = applicationDao;
    this.testDataService = testDataService;
    lifecycle.addStopHook(() -> {
      EXECUTOR.shutdown();
      return CompletableFuture.completedFuture(null);
    });
    EXECUTOR.schedule(this::startUp, 3, TimeUnit.SECONDS);
  }

  private void startUp() {
    long applicationCount = applicationDao.getApplicationCount();
    if (applicationCount == 0) {
      testDataService.deleteAllUsers();
      testDataService.deleteCurrentUserAndInsertTwoCompanies();
    }
  }

}

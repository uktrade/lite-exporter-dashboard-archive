package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import models.Application;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;

public class ApplicationDaoImpl implements ApplicationDao {

  private final DBI dbi;

  @Inject
  public ApplicationDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<Application> getApplications() {
    try (final Handle handle = dbi.open()) {
      ApplicationJDBIDao applicationJDBIDao = handle.attach(ApplicationJDBIDao.class);
      return applicationJDBIDao.getApplications();
    }
  }

  @Override
  public void insert(Application application) {
    try (final Handle handle = dbi.open()) {
      ApplicationJDBIDao applicationJDBIDao = handle.attach(ApplicationJDBIDao.class);
      applicationJDBIDao.insert(application.getAppId(),
          application.getCompanyId(),
          application.getApplicationStatus(),
          application.getApplicantReference(),
          JsonUtil.convertListToJson(application.getDestinationList()),
          application.getCaseReference(),
          application.getCaseOfficerId());
    }
  }

  @Override
  public void deleteAllApplications() {
    try (final Handle handle = dbi.open()) {
      ApplicationJDBIDao applicationJDBIDao = handle.attach(ApplicationJDBIDao.class);
      applicationJDBIDao.truncateTable();
    }
  }

}

package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import models.Application;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class ApplicationDaoImpl implements ApplicationDao {

  private final DBI dbi;

  @Inject
  public ApplicationDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public long getApplicationCount() {
    try (final Handle handle = dbi.open()) {
      ApplicationJDBIDao applicationJDBIDao = handle.attach(ApplicationJDBIDao.class);
      return applicationJDBIDao.getApplicationCount();
    }
  }

  @Override
  public List<Application> getApplications(List<String> customerIds) {
    if (customerIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      try (final Handle handle = dbi.open()) {
        ApplicationJDBIDao applicationJDBIDao = handle.attach(ApplicationJDBIDao.class);
        return applicationJDBIDao.getApplications(customerIds);
      }
    }
  }

  @Override
  public Application getApplication(String id) {
    try (final Handle handle = dbi.open()) {
      ApplicationJDBIDao applicationJDBIDao = handle.attach(ApplicationJDBIDao.class);
      return applicationJDBIDao.getApplication(id);
    }
  }

  @Override
  public void insert(Application application) {
    try (final Handle handle = dbi.open()) {
      ApplicationJDBIDao applicationJDBIDao = handle.attach(ApplicationJDBIDao.class);
      applicationJDBIDao.insert(application.getId(),
          application.getCustomerId(),
          application.getCreatedByUserId(),
          application.getCreatedTimestamp(),
          application.getSubmittedTimestamp(),
          JsonUtil.convertListToJson(application.getConsigneeCountries()),
          JsonUtil.convertListToJson(application.getEndUserCountries()),
          application.getApplicantReference(),
          application.getCaseReference(),
          application.getCaseOfficerId(),
          application.getSiteId());
    }
  }

  @Override
  public void deleteAllApplications() {
    try (final Handle handle = dbi.open()) {
      ApplicationJDBIDao applicationJDBIDao = handle.attach(ApplicationJDBIDao.class);
      applicationJDBIDao.truncateTable();
    }
  }


  @Override
  public void deleteApplication(String id) {
    try (final Handle handle = dbi.open()) {
      ApplicationJDBIDao applicationJDBIDao = handle.attach(ApplicationJDBIDao.class);
      applicationJDBIDao.delete(id);
    }
  }

}

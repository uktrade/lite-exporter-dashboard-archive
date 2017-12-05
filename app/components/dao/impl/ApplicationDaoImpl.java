package components.dao.impl;

import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.jdbi.ApplicationJDBIDao;
import components.util.JsonUtil;
import models.Application;
import org.skife.jdbi.v2.DBI;

import java.util.ArrayList;
import java.util.List;

public class ApplicationDaoImpl implements ApplicationDao {

  private final ApplicationJDBIDao applicationJDBIDao;

  @Inject
  public ApplicationDaoImpl(DBI dbi) {
    this.applicationJDBIDao = dbi.onDemand(ApplicationJDBIDao.class);
  }

  @Override
  public long getApplicationCount() {
    return applicationJDBIDao.getApplicationCount();
  }

  @Override
  public Application getApplication(String id) {
    return applicationJDBIDao.getApplication(id);
  }

  @Override
  public List<Application> getApplicationsByCustomerIds(List<String> customerIds) {
    if (customerIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      return applicationJDBIDao.getApplicationsByCustomerIds(customerIds);
    }
  }

  @Override
  public void update(Application application) {
    applicationJDBIDao.update(application.getId(),
        application.getCustomerId(),
        application.getCreatedByUserId(),
        application.getCreatedTimestamp(),
        application.getSubmittedTimestamp(),
        JsonUtil.convertListToJson(application.getConsigneeCountries()),
        JsonUtil.convertListToJson(application.getEndUserCountries()),
        application.getApplicantReference(),
        application.getCaseOfficerId(),
        application.getSiteId());
  }

  @Override
  public void deleteAllApplications() {
    applicationJDBIDao.truncateTable();
  }

  @Override
  public void deleteApplication(String id) {
    applicationJDBIDao.deleteApplication(id);
  }

}

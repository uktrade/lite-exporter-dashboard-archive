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
  public List<Application> getApplicationsByCustomerIdsAndUserId(List<String> customerIds, String userId) {
    if (userId == null) {
      return new ArrayList<>();
    } else if (customerIds.isEmpty()) {
      return applicationJDBIDao.getApplicationsByUserId(userId);
    } else {
      return applicationJDBIDao.getApplicationsByCustomerIdsAndUserId(customerIds, userId);
    }
  }

  @Override
  public void updateCaseOfficerId(String id, String caseOfficerId) {
    applicationJDBIDao.updateCaseOfficerId(id, caseOfficerId);
  }

  @Override
  public void updateApplicantReference(String id, String applicantReference) {
    applicationJDBIDao.updateApplicantReference(id, applicantReference);
  }

  @Override
  public void updateCustomerId(String id, String customerId) {
    applicationJDBIDao.updateCustomerId(id, customerId);
  }

  @Override
  public void updateSiteId(String id, String siteId) {
    applicationJDBIDao.updateSiteId(id, siteId);
  }

  @Override
  public void updateCountries(String id, List<String> consigneeCountries, List<String> endUserCountries) {
    applicationJDBIDao.updateCountries(id,
        JsonUtil.convertListToJson(consigneeCountries),
        JsonUtil.convertListToJson(endUserCountries));
  }

  @Override
  public void insert(String id, String createdByUserId, Long createdTimestamp) {
    applicationJDBIDao.insert(id,
        createdByUserId,
        createdTimestamp,
        JsonUtil.convertListToJson(new ArrayList<>()),
        JsonUtil.convertListToJson(new ArrayList<>()));
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

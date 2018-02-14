package components.dao;

import models.Application;

import java.util.List;

public interface ApplicationDao {

  long getApplicationCount();

  Application getApplication(String id);

  List<Application> getApplicationsByCustomerIdsAndUserId(List<String> customerIds, String userId);

  void updateCaseOfficerId(String id, String caseOfficerId);

  void updateApplicantReference(String id, String applicantReference);

  void updateCustomerId(String id, String customerId);

  void updateSiteId(String id, String siteId);

  void updateCountries(String id, List<String> consigneeCountries, List<String> endUserCountries);

  void insert(String id, String createdByUserId, Long createdTimestamp);

  void deleteAllApplications();

  void deleteApplication(String id);

}

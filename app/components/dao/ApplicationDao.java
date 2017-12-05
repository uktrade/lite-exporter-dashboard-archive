package components.dao;

import models.Application;

import java.util.List;

public interface ApplicationDao {

  long getApplicationCount();

  Application getApplication(String id);

  List<Application> getApplicationsByCustomerIds(List<String> customerIds);

  void update(Application application);

  void deleteAllApplications();

  void deleteApplication(String id);

}

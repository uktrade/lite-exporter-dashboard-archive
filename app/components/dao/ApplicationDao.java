package components.dao;

import java.util.List;
import models.Application;

public interface ApplicationDao {

  long getApplicationCount();

  Application getApplication(String id);

  List<Application> getApplications(String id);

  List<Application> getApplicationsByCustomerIds(List<String> customerIds);

  void insert(Application application);

  void deleteAllApplications();

  void deleteApplication(String id);

}

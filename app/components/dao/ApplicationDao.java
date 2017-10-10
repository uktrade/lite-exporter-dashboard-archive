package components.dao;

import java.util.List;
import models.Application;

public interface ApplicationDao {

  long getApplicationCount();

  List<Application> getApplications(List<String> customerIds);

  Application getApplication(String id);

  void insert(Application application);

  void deleteAllApplications();

  void deleteApplication(String id);

}

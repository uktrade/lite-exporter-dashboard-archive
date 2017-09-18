package components.dao;

import models.Application;

import java.util.List;

public interface ApplicationDao {

  long getApplicationCount();

  List<Application> getApplications(List<String> customerIds);

  Application getApplication(String appId);

  void insert(Application application);

  void deleteAllApplications();

  void deleteApplication(String appId);

}

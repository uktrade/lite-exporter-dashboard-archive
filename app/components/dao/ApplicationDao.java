package components.dao;

import models.Application;

import java.util.List;

public interface ApplicationDao {

  List<Application> getApplications();

  Application getApplication(String appId);

  void insert(Application application);

  void deleteAllApplications();

}

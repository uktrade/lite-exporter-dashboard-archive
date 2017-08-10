package components.dao;

import models.Application;

import java.util.List;

public interface ApplicationDao {

  List<Application> getApplications();

  void insert(Application application);

  void deleteAllApplications();

}

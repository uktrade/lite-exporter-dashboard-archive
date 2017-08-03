package components.dao;

import models.StatusUpdate;

import java.util.List;

public interface StatusUpdateDao {

  List<StatusUpdate> getStatusUpdates(String appId);

  void insertStatusUpdate(StatusUpdate statusUpdate);

}

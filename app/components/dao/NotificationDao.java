package components.dao;

import java.util.List;
import models.Notification;

public interface NotificationDao {

  List<Notification> getNotifications(List<String> caseReferences);

  void insertNotification(Notification notification);

  void deleteNotifications(String caseReference);

  void deleteAllNotifications();

}

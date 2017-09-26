package components.dao;

import models.Notification;

import java.util.List;

public interface NotificationDao {

  List<Notification> getNotifications(String appId);

  void insertNotification(Notification notification);

  void deleteNotificationsByAppId(String appId);

  void deleteAllNotifications();

}

package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import models.Notification;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.ArrayList;
import java.util.List;

public class NotificationDaoImpl implements NotificationDao {

  final DBI dbi;

  @Inject
  public NotificationDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<Notification> getNotifications(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      try (Handle handle = dbi.open()) {
        NotificationJDBIDao notificationJDBIDao = handle.attach(NotificationJDBIDao.class);
        return notificationJDBIDao.getNotifications(appIds);
      }
    }
  }

  @Override
  public List<Notification> getNotifications(String appId) {
    try (Handle handle = dbi.open()) {
      NotificationJDBIDao notificationJDBIDao = handle.attach(NotificationJDBIDao.class);
      return notificationJDBIDao.getNotifications(appId);
    }
  }

  @Override
  public void insertNotification(Notification notification) {
    try (Handle handle = dbi.open()) {
      NotificationJDBIDao notificationJDBIDao = handle.attach(NotificationJDBIDao.class);
      notificationJDBIDao.insertNotification(notification.getId(),
          notification.getAppId(),
          notification.getNotificationType(),
          notification.getCreatedByUserId(),
          notification.getCreatedTimestamp(),
          JsonUtil.convertListToJson(notification.getRecipientUserIds()),
          notification.getMessage(),
          JsonUtil.convertFileToJson(notification.getDocument()));
    }
  }

  @Override
  public void deleteNotificationsByAppId(String appId) {
    try (Handle handle = dbi.open()) {
      NotificationJDBIDao notificationJDBIDao = handle.attach(NotificationJDBIDao.class);
      notificationJDBIDao.deleteNotificationByAppId(appId);
    }
  }

  @Override
  public void deleteAllNotifications() {
    try (Handle handle = dbi.open()) {
      NotificationJDBIDao notificationJDBIDao = handle.attach(NotificationJDBIDao.class);
      notificationJDBIDao.truncateTable();
    }
  }

}

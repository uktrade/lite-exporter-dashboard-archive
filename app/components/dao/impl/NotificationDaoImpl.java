package components.dao.impl;

import com.google.inject.Inject;
import components.dao.NotificationDao;
import components.dao.jdbi.NotificationJDBIDao;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import models.Notification;
import org.skife.jdbi.v2.DBI;

public class NotificationDaoImpl implements NotificationDao {

  private final NotificationJDBIDao notificationJDBIDao;

  @Inject
  public NotificationDaoImpl(DBI dbi) {
    this.notificationJDBIDao = dbi.onDemand(NotificationJDBIDao.class);
  }

  @Override
  public List<Notification> getNotifications(List<String> caseReferences) {
    if (caseReferences.isEmpty()) {
      return new ArrayList<>();
    } else {
      return notificationJDBIDao.getNotifications(caseReferences);
    }
  }

  @Override
  public void insertNotification(Notification notification) {
    notificationJDBIDao.insertNotification(notification.getId(),
        notification.getCaseReference(),
        notification.getNotificationType(),
        notification.getCreatedByUserId(),
        notification.getCreatedTimestamp(),
        JsonUtil.convertListToJson(notification.getRecipientUserIds()),
        notification.getMessage(),
        JsonUtil.convertFileToJson(notification.getDocument()));
  }

  @Override
  public void deleteNotifications(String caseReference) {
    notificationJDBIDao.deleteNotifications(caseReference);
  }

  @Override
  public void deleteAllNotifications() {
    notificationJDBIDao.truncateTable();
  }

}

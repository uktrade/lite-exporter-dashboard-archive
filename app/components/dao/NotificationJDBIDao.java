package components.dao;

import models.Notification;
import models.NotificationType;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public interface NotificationJDBIDao {

  @Mapper(NotificationRSMapper.class)
  @SqlQuery("SELECT * FROM NOTIFICATION WHERE APP_ID = :appId")
  List<Notification> getNotifications(@Bind("appId") String appId);

  @SqlUpdate("INSERT INTO NOTIFICATION (ID, APP_ID,  NOTIFICATION_TYPE, CREATED_BY_USER_ID, CREATED_TIMESTAMP, RECIPIENT_USER_IDS, MESSAGE,  ATTACHMENTS) VALUES " +
      "                               (:id, :appId, :notificationType, :createdByUserId,   :createdTimestamp, :recipientUserIds,  :message, :attachments)")
  void insertNotification(@Bind("id") String id,
                          @Bind("appId") String appId,
                          @Bind("notificationType") NotificationType notificationType,
                          @Bind("createdByUserId") String createdByUserId,
                          @Bind("createdTimestamp") Long createdTimestamp,
                          @Bind("recipientUserIds") String recipientUserIds,
                          @Bind("message") String message,
                          @Bind("attachments") String attachments);

  @SqlUpdate("DELETE FROM NOTIFICATION WHERE APP_ID = :appId")
  void deleteNotificationByAppId(@Bind("appId") String appId);

  @SqlUpdate("DELETE FROM NOTIFICATION")
  void truncateTable();

}

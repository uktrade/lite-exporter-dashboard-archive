package components.dao;

import models.Notification;
import models.NotificationType;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

@UseStringTemplate3StatementLocator
public interface NotificationJDBIDao {

  @Mapper(NotificationRSMapper.class)
  @SqlQuery("SELECT * FROM NOTIFICATION WHERE APP_ID in (<appIds>)")
  List<Notification> getNotifications(@BindIn("appIds") List<String> appIds);

  @Mapper(NotificationRSMapper.class)
  @SqlQuery("SELECT * FROM NOTIFICATION WHERE APP_ID = :appId")
  List<Notification> getNotifications(@Bind("appId") String appId);

  @SqlUpdate("INSERT INTO NOTIFICATION (ID,  APP_ID, NOTIFICATION_TYPE, CREATED_BY_USER_ID, CREATED_TIMESTAMP, RECIPIENT_USER_IDS, MESSAGE,  DOCUMENT) VALUES " +
      "                               (:id, :appId, :notificationType, :createdByUserId,   :createdTimestamp, :recipientUserIds,  :message, :document)")
  void insertNotification(@Bind("id") String id,
                          @Bind("appId") String appId,
                          @Bind("notificationType") NotificationType notificationType,
                          @Bind("createdByUserId") String createdByUserId,
                          @Bind("createdTimestamp") Long createdTimestamp,
                          @Bind("recipientUserIds") String recipientUserIds,
                          @Bind("message") String message,
                          @Bind("document") String document);

  @SqlUpdate("DELETE FROM NOTIFICATION WHERE APP_ID = :appId")
  void deleteNotificationByAppId(@Bind("appId") String appId);

  @SqlUpdate("DELETE FROM NOTIFICATION")
  void truncateTable();

}

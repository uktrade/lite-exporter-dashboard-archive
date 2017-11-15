package components.dao.jdbi;

import components.dao.mapper.NotificationRSMapper;
import java.util.List;
import models.Notification;
import models.NotificationType;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

@UseStringTemplate3StatementLocator
public interface NotificationJDBIDao {

  @Mapper(NotificationRSMapper.class)
  @SqlQuery("SELECT * FROM NOTIFICATION WHERE CASE_REFERENCE in (<caseReferences>)")
  List<Notification> getNotifications(@BindIn("caseReferences") List<String> caseReferences);

  @SqlUpdate("INSERT INTO NOTIFICATION (ID,  CASE_REFERENCE, NOTIFICATION_TYPE, CREATED_BY_USER_ID, CREATED_TIMESTAMP, RECIPIENT_USER_IDS, MESSAGE,  DOCUMENT) VALUES " +
      "                               (:id, :caseReference, :notificationType, :createdByUserId,   :createdTimestamp, :recipientUserIds,  :message, :document)")
  void insertNotification(@Bind("id") String id,
                          @Bind("caseReference") String caseReference,
                          @Bind("notificationType") NotificationType notificationType,
                          @Bind("createdByUserId") String createdByUserId,
                          @Bind("createdTimestamp") Long createdTimestamp,
                          @Bind("recipientUserIds") String recipientUserIds,
                          @Bind("message") String message,
                          @Bind("document") String document);

  @SqlUpdate("DELETE FROM NOTIFICATION WHERE CASE_REFERENCE = :caseReference")
  void deleteNotifications(@Bind("caseReference") String caseReference);

  @SqlUpdate("DELETE FROM NOTIFICATION")
  void truncateTable();

}

package components.dao;

import components.util.JsonUtil;
import models.Notification;
import models.NotificationType;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import uk.gov.bis.lite.exporterdashboard.api.File;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class NotificationRSMapper implements ResultSetMapper<Notification> {

  @Override
  public Notification map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String appId = r.getString("app_id");
    NotificationType notificationType = NotificationType.valueOf(r.getString("notification_type"));
    String createdByUserId = r.getString("created_by_user_id");
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    String recipientUserIdsJson = r.getString("recipient_user_ids");
    List<String> recipientUserIds = JsonUtil.convertJsonToList(recipientUserIdsJson);
    String message = r.getString("message");
    String attachmentsJson = r.getString("attachments");
    List<File> attachments = JsonUtil.convertJsonToFiles(attachmentsJson);
    return new Notification(id, appId, notificationType, createdByUserId, createdTimestamp, recipientUserIds, message, attachments);
  }

}

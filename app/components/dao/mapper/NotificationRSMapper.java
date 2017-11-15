package components.dao.mapper;

import components.dao.helper.LongMapper;
import components.util.JsonUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import models.File;
import models.Notification;
import models.NotificationType;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class NotificationRSMapper implements ResultSetMapper<Notification> {

  @Override
  public Notification map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String caseReferences = r.getString("case_reference");
    NotificationType notificationType = NotificationType.valueOf(r.getString("notification_type"));
    String createdByUserId = r.getString("created_by_user_id");
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    String recipientUserIdsJson = r.getString("recipient_user_ids");
    List<String> recipientUserIds = JsonUtil.convertJsonToList(recipientUserIdsJson);
    String message = r.getString("message");
    String documentJson = r.getString("document");
    File document = JsonUtil.convertJsonToFile(documentJson);
    return new Notification(id, caseReferences, notificationType, createdByUserId, createdTimestamp, recipientUserIds, message, document);
  }

}

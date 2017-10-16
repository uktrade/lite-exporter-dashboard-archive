package components.dao;

import components.util.JsonUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import models.Rfi;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class RfiRSMapper implements ResultSetMapper<Rfi> {

  @Override
  public Rfi map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String appId = r.getString("app_id");
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    Long dueTimestamp = LongMapper.getLong(r, "due_timestamp");
    String createdByUserId = r.getString("created_by_user_id");
    String recipientUserIdsJson = r.getString("recipient_user_ids");
    List<String> recipientUserIds = JsonUtil.convertJsonToList(recipientUserIdsJson);
    String message = r.getString("message");
    return new Rfi(id, appId, createdTimestamp, dueTimestamp, createdByUserId, recipientUserIds, message);
  }

}

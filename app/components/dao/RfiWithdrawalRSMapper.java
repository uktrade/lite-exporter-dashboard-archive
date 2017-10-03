package components.dao;

import components.util.JsonUtil;
import models.RfiWithdrawal;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RfiWithdrawalRSMapper implements ResultSetMapper<RfiWithdrawal> {

  @Override
  public RfiWithdrawal map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String rifId = r.getString("rfi_id");
    String createdByUserId = r.getString("created_by_user_id");
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    String recipientUserIdsJson = r.getString("recipient_user_ids");
    List<String> recipientUserIds = JsonUtil.convertJsonToList(recipientUserIdsJson);
    String message = r.getString("message");
    return new RfiWithdrawal(id, rifId, createdByUserId, createdTimestamp, recipientUserIds, message);
  }

}

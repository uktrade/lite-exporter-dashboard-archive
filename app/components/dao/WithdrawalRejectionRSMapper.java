package components.dao;

import models.WithdrawalRejection;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WithdrawalRejectionRSMapper implements ResultSetMapper<WithdrawalRejection> {

  @Override
  public WithdrawalRejection map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String appId = r.getString("app_id");
    String createdByUserId = r.getString("created_by_user_id");
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    String message = r.getString("message");
    return new WithdrawalRejection(id, appId, createdByUserId, createdTimestamp, message);
  }

}

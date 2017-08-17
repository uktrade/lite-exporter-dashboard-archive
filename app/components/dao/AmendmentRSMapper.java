package components.dao;

import models.Amendment;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AmendmentRSMapper implements ResultSetMapper<Amendment> {
  @Override
  public Amendment map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String amendmentId = r.getString("amendment_id");
    String appId = r.getString("app_id");
    Long sentTimestamp = r.getLong("sent_timestamp");
    String sentBy = r.getString("sent_by");
    String message = r.getString("message");
    String attachments = r.getString("attachments");
    return new Amendment(amendmentId, appId, sentTimestamp, sentBy, message, attachments);
  }
}

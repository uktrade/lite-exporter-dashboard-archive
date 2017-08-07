package components.dao;

import models.Rfi;
import models.enums.RfiStatus;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RfiRSMapper implements ResultSetMapper<Rfi> {

  @Override
  public Rfi map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String rfiId = r.getString("rfi_id");
    String appId = r.getString("app_id");
    RfiStatus rfiStatus = RfiStatus.valueOf(r.getString("status"));
    Long receivedTimestamp = LongMapper.getLong(r, "received_timestamp");
    Long dueTimestamp = LongMapper.getLong(r, "due_timestamp");
    String sentBy = r.getString("sent_by");
    String message = r.getString("message");
    return new Rfi(rfiId, appId, rfiStatus, receivedTimestamp, dueTimestamp, sentBy, message);
  }
}

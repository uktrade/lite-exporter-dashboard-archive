package components.dao;

import models.RfiResponse;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RfiResponseRSMapper implements ResultSetMapper<RfiResponse> {

  @Override
  public RfiResponse map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String rfiId = r.getString("rfi_id");
    String sentBy = r.getString("sent_by");
    Long sentTimestamp = LongMapper.getLong(r, "sent_timestamp");
    String message = r.getString("message");
    String attachments = r.getString("attachments");
    return new RfiResponse(rfiId, sentBy, sentTimestamp, message, attachments);
  }

}

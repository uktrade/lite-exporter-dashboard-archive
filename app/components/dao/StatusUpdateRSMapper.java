package components.dao;

import models.StatusUpdate;
import models.enums.StatusType;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatusUpdateRSMapper implements ResultSetMapper<StatusUpdate> {

  @Override
  public StatusUpdate map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String appId = r.getString("app_id");
    StatusType statusType = StatusType.valueOf(r.getString("status_type"));
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    return new StatusUpdate(appId, statusType, createdTimestamp);
  }

}

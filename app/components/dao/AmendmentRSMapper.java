package components.dao;

import components.util.JsonUtil;
import models.Amendment;
import models.File;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AmendmentRSMapper implements ResultSetMapper<Amendment> {

  @Override
  public Amendment map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String amendmentId = r.getString("amendment_id");
    String appId = r.getString("app_id");
    Long sentTimestamp = LongMapper.getLong(r, "sent_timestamp");
    String sentBy = r.getString("sent_by");
    String message = r.getString("message");
    String attachmentsJson = r.getString("attachments");
    List<File> attachments = JsonUtil.convertJsonToFiles(attachmentsJson);
    return new Amendment(amendmentId, appId, sentTimestamp, sentBy, message, attachments);
  }

}

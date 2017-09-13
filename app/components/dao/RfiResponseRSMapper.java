package components.dao;

import components.util.JsonUtil;
import models.File;
import models.RfiResponse;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RfiResponseRSMapper implements ResultSetMapper<RfiResponse> {

  @Override
  public RfiResponse map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String rfiId = r.getString("rfi_id");
    String sentBy = r.getString("sent_by");
    Long sentTimestamp = LongMapper.getLong(r, "sent_timestamp");
    String message = r.getString("message");
    String attachmentsJson = r.getString("attachments");
    List<File> attachments = JsonUtil.convertJsonToFiles(attachmentsJson);
    return new RfiResponse(rfiId, sentBy, sentTimestamp, message, attachments);
  }

}

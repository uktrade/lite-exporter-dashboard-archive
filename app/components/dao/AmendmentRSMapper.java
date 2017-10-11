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
    String id = r.getString("id");
    String appId = r.getString("app_id");
    String createdByUserId = r.getString("created_by_user_id");
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    String message = r.getString("message");
    String attachmentsJson = r.getString("attachments");
    List<File> attachments = JsonUtil.convertJsonToFiles(attachmentsJson);

    Amendment amendment = new Amendment();
    amendment.setId(id);
    amendment.setAppId(appId);
    amendment.setCreatedByUserId(createdByUserId);
    amendment.setCreatedTimestamp(createdTimestamp);
    amendment.setMessage(message);
    amendment.setAttachments(attachments);
    return amendment;
  }

}

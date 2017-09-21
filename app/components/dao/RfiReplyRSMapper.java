package components.dao;

import components.util.JsonUtil;
import uk.gov.bis.lite.exporterdashboard.api.File;
import uk.gov.bis.lite.exporterdashboard.api.RfiReply;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RfiReplyRSMapper implements ResultSetMapper<RfiReply> {

  @Override
  public RfiReply map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String rfiId = r.getString("rfi_id");
    String createdByUserId = r.getString("created_by_user_id");
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    String message = r.getString("message");
    String attachmentsJson = r.getString("attachments");
    List<File> attachments = JsonUtil.convertJsonToFiles(attachmentsJson);

    RfiReply rfiReply = new RfiReply();
    rfiReply.setId(id);
    rfiReply.setRfiId(rfiId);
    rfiReply.setCreatedByUserId(createdByUserId);
    rfiReply.setCreatedTimestamp(createdTimestamp);
    rfiReply.setMessage(message);
    rfiReply.setAttachments(attachments);
    return rfiReply;
  }

}

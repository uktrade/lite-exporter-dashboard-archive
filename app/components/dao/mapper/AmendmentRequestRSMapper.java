package components.dao.mapper;

import components.dao.helper.LongMapper;
import components.util.JsonUtil;
import models.AmendmentRequest;
import models.Attachment;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AmendmentRequestRSMapper implements ResultSetMapper<AmendmentRequest> {

  @Override
  public AmendmentRequest map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String appId = r.getString("app_id");
    String createdByUserId = r.getString("created_by_user_id");
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    String message = r.getString("message");
    String attachmentsJson = r.getString("attachments");
    List<Attachment> attachments = JsonUtil.convertJsonToAttachments(attachmentsJson);

    AmendmentRequest amendmentRequest = new AmendmentRequest();
    amendmentRequest.setId(id);
    amendmentRequest.setAppId(appId);
    amendmentRequest.setCreatedByUserId(createdByUserId);
    amendmentRequest.setCreatedTimestamp(createdTimestamp);
    amendmentRequest.setMessage(message);
    amendmentRequest.setAttachments(attachments);
    return amendmentRequest;
  }

}

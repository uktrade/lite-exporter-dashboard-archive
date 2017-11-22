package components.dao.mapper;

import components.dao.helper.LongMapper;
import components.util.JsonUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import models.File;
import models.WithdrawalRequest;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class WithdrawalRequestRSMapper implements ResultSetMapper<WithdrawalRequest> {

  @Override
  public WithdrawalRequest map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String appId = r.getString("app_id");
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    String createdByUserId = r.getString("created_by_user_id");
    String message = r.getString("message");
    String attachmentsJson = r.getString("attachments");
    List<File> attachments = JsonUtil.convertJsonToFiles(attachmentsJson);

    WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
    withdrawalRequest.setId(id);
    withdrawalRequest.setAppId(appId);
    withdrawalRequest.setCreatedByUserId(createdByUserId);
    withdrawalRequest.setCreatedTimestamp(createdTimestamp);
    withdrawalRequest.setMessage(message);
    withdrawalRequest.setAttachments(attachments);
    return withdrawalRequest;
  }

}

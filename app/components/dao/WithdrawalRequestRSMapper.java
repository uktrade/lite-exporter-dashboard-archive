package components.dao;

import models.WithdrawalRequest;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WithdrawalRequestRSMapper implements ResultSetMapper<WithdrawalRequest> {

  @Override
  public WithdrawalRequest map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String withdrawalRequestId = r.getString("withdrawal_request_id");
    String appId = r.getString("app_id");
    Long sentTimestamp = r.getLong("sent_timestamp");
    String sentBy = r.getString("sentBy");
    String message = r.getString("message");
    String attachments = r.getString("attachments");
    String rejectedBy = r.getString("rejected_by");
    Long rejectedTimestamp = LongMapper.getLong(r, "rejected_timestamp");
    String rejectedMessage = r.getString("rejected_message");
    return new WithdrawalRequest(withdrawalRequestId, appId, sentTimestamp, sentBy, message, attachments, rejectedBy, rejectedTimestamp, rejectedMessage);
  }

}

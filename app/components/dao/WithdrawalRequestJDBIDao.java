package components.dao;

import models.WithdrawalRequest;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

@UseStringTemplate3StatementLocator
public interface WithdrawalRequestJDBIDao {

  @Mapper(WithdrawalRequestRSMapper.class)
  @SqlQuery("SELECT * FROM WITHDRAWAL_REQUEST WHERE APP_ID = :appId")
  WithdrawalRequest getWithdrawalRequest(String appId);

  @SqlUpdate("INSERT INTO WITHDRAWAL_REQUEST (WITHDRAWAL_REQUEST_ID, APP_ID, SENT_TIMESTAMP, SENT_BY, MESSAGE,  ATTACHMENTS,  REJECTED_BY, REJECTED_TIMESTAMP, REJECTED_MESSAGE) VALUES " +
      "                                     (:withdrawalRequestId,  :appId, :sentTimestamp, :sentBy, :message, :attachments, :rejectedBy, :rejectedTimestamp, :rejectedMessage)")
  void insert(@Bind("withdrawalRequestId") String withdrawalRequestId,
              @Bind("appId") String appId,
              @Bind("sentTimestamp") Long sentTimestamp,
              @Bind("sentBy") String sentBy,
              @Bind("message") String message,
              @Bind("attachments") String attachments,
              @Bind("rejectedBy") String rejectedBy,
              @Bind("rejectedTimestamp") Long rejectedTimestamp,
              @Bind("rejectedMessage") String rejectedMessage);

  @SqlUpdate("DELETE FROM WITHDRAWAL_REQUEST")
  void truncateTable();

  @SqlUpdate("DELETE FROM WITHDRAWAL_REQUEST WHERE APP_ID in (<appIds>)")
  void deleteWithdrawalRequestsByAppIds(@BindIn("appIds") List<String> appIds);

}

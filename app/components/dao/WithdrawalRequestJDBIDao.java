package components.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

import java.util.List;

public interface WithdrawalRequestJDBIDao {

  @Mapper(WithdrawalRequestRSMapper.class)
  @SqlQuery("SELECT * FROM WITHDRAWAL_REQUEST WHERE APP_ID = :appId")
  List<WithdrawalRequest> getWithdrawalRequests(@Bind("appId") String appId);

  @SqlUpdate("INSERT INTO WITHDRAWAL_REQUEST (ID,  APP_ID, CREATED_BY_USER_ID, CREATED_TIMESTAMP, MESSAGE,  ATTACHMENTS) VALUES " +
      "                                     (:id, :appId, :createdByUserId,   :createdTimestamp, :message, :attachments)")
  void insert(@Bind("id") String id,
              @Bind("appId") String appId,
              @Bind("createdByUserId") String createdByUserId,
              @Bind("createdTimestamp") Long createdTimestamp,
              @Bind("message") String message,
              @Bind("attachments") String attachments);

  @SqlUpdate("DELETE FROM WITHDRAWAL_REQUEST")
  void truncateTable();

  @SqlUpdate("DELETE FROM WITHDRAWAL_REQUEST WHERE APP_ID = :appId")
  void deleteWithdrawalRequestsByAppId(@Bind("appId") String appId);

}

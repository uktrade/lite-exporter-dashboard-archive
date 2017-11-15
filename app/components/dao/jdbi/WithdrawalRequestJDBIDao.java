package components.dao.jdbi;

import components.dao.mapper.WithdrawalRequestRSMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;
import models.WithdrawalRequest;

import java.util.List;

@UseStringTemplate3StatementLocator
public interface WithdrawalRequestJDBIDao {

  @Mapper(WithdrawalRequestRSMapper.class)
  @SqlQuery("SELECT * FROM WITHDRAWAL_REQUEST WHERE APP_ID = :appId")
  List<WithdrawalRequest> getWithdrawalRequestsByAppId(@Bind("appId") String appId);

  @Mapper(WithdrawalRequestRSMapper.class)
  @SqlQuery("SELECT * FROM WITHDRAWAL_REQUEST WHERE APP_ID in (<appIds>)")
  List<WithdrawalRequest> getWithdrawalRequestsByAppIds(@BindIn("appIds") List<String> appIds);

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

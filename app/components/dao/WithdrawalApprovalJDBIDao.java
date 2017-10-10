package components.dao;

import models.WithdrawalApproval;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

@UseStringTemplate3StatementLocator
public interface WithdrawalApprovalJDBIDao {

  @Mapper(WithdrawalApprovalRSMapper.class)
  @SqlQuery("SELECT * FROM WITHDRAWAL_APPROVAL WHERE APP_ID = :appId")
  WithdrawalApproval getWithdrawalApproval(@Bind("appId") String appId);

  @Mapper(WithdrawalApprovalRSMapper.class)
  @SqlQuery("SELECT * FROM WITHDRAWAL_APPROVAL WHERE APP_ID in (<appIds>)")
  List<WithdrawalApproval> getWithdrawalApprovals(@BindIn("appIds") List<String> appIds);

  @SqlUpdate("INSERT INTO WITHDRAWAL_APPROVAL (id,  app_id, created_by_user_id, created_timestamp, message) VALUES " +
      "                                      (:id, :appId, :createdByUserId,   :createdTimestamp, :message)")
  void insertWithdrawalApproval(@Bind("id") String id,
                                @Bind("appId") String appId,
                                @Bind("createdByUserId") String createdByUserId,
                                @Bind("createdTimestamp") Long createdTimestamp,
                                @Bind("message") String message);

  @SqlUpdate("DELETE FROM WITHDRAWAL_APPROVAL WHERE APP_ID = :appId")
  void deleteWithdrawalApprovalsByAppId(@Bind("appId") String appId);

  @SqlUpdate("DELETE FROM WITHDRAWAL_APPROVAL")
  void truncateTable();

}
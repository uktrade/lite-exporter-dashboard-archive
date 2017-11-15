package components.dao.jdbi;

import components.dao.mapper.WithdrawalRejectionRSMapper;
import java.util.List;
import models.WithdrawalRejection;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

@UseStringTemplate3StatementLocator
public interface WithdrawalRejectionJDBIDao {

  @Mapper(WithdrawalRejectionRSMapper.class)
  @SqlQuery("SELECT * FROM WITHDRAWAL_REJECTION WHERE APP_ID = :appId")
  List<WithdrawalRejection> getWithdrawalRejectionsByAppId(@Bind("appId") String appId);

  @Mapper(WithdrawalRejectionRSMapper.class)
  @SqlQuery("SELECT * FROM WITHDRAWAL_REJECTION WHERE APP_ID in (<appIds>)")
  List<WithdrawalRejection> getWithdrawalRejectionsByAppIds(@BindIn("appIds") List<String> appIds);

  @SqlUpdate("INSERT INTO WITHDRAWAL_REJECTION (ID,  APP_ID, CREATED_BY_USER_ID, CREATED_TIMESTAMP, RECIPIENT_USER_IDS,  MESSAGE) VALUES " +
      "                                       (:id, :appId, :createdByUserId,   :createdTimestamp, :recipientUserIds,   :message)")
  void insertWithdrawalRejection(@Bind("id") String id,
                                 @Bind("appId") String appId,
                                 @Bind("createdByUserId") String createdByUserId,
                                 @Bind("createdTimestamp") Long createdTimestamp,
                                 @Bind("recipientUserIds") String recipientUserIds,
                                 @Bind("message") String message);

  @SqlUpdate("DELETE FROM WITHDRAWAL_REJECTION WHERE APP_ID = :appId")
  void deleteWithdrawalRejectionsByAppId(@Bind("appId") String appId);

  @SqlUpdate("DELETE FROM WITHDRAWAL_REJECTION")
  void truncateTable();

}

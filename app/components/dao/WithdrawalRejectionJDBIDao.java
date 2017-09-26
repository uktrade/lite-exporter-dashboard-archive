package components.dao;

import models.WithdrawalRejection;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public interface WithdrawalRejectionJDBIDao {

  @Mapper(WithdrawalRejectionRSMapper.class)
  @SqlQuery("SELECT * FROM WITHDRAWAL_REJECTION WHERE APP_ID = :appId")
  List<WithdrawalRejection> getWithdrawalRejections(@Bind("appId") String appId);

  @SqlUpdate("INSERT INTO WITHDRAWAL_REJECTION (id,  app_id, created_by_user_id, created_timestamp, message) VALUES " +
      "                                       (:id, :appId, :createdByUserId,   :createdTimestamp, :message)")
  void insertWithdrawalRejection(@Bind("id") String id,
                                 @Bind("appId") String appId,
                                 @Bind("createdByUserId") String createdByUserId,
                                 @Bind("createdTimestamp") Long createdTimestamp,
                                 @Bind("message") String message);

  @SqlUpdate("DELETE FROM WITHDRAWAL_REJECTION WHERE APP_ID = :appId")
  void deleteWithdrawalRejectionsByAppId(@Bind("appId") String appId);

  @SqlUpdate("DELETE FROM WITHDRAWAL_REJECTION")
  void truncateTable();

}

package components.dao;

import models.StatusUpdate;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

@UseStringTemplate3StatementLocator
public interface StatusUpdateJDBIDao {

  @Mapper(StatusUpdateRSMapper.class)
  @SqlQuery("SELECT * FROM STATUS_UPDATE WHERE APP_ID = :appId")
  List<StatusUpdate> getStatusUpdates(@Bind("appId") String appId);

  @Mapper(StatusUpdateRSMapper.class)
  @SqlQuery("SELECT * FROM STATUS_UPDATE WHERE APP_ID in (<appIds>)")
  List<StatusUpdate> getStatusUpdates(@BindIn("appIds") List<String> appIds);

  @Mapper(StatusUpdateRSMapper.class)
  @SqlQuery("SELECT * FROM STATUS_UPDATE WHERE APP_ID = :appId AND STATUS_TYPE = :statusType")
  StatusUpdate getStatusUpdate(@Bind("appId") String appId, @Bind("statusType") String statusType);

  @SqlUpdate("INSERT INTO STATUS_UPDATE (APP_ID, STATUS_TYPE, START_TIMESTAMP, END_TIMESTAMP) VALUES (:appId, :statusType, :startTimestamp, :endTimestamp)")
  void insert(@Bind("appId") String appId, @Bind("statusType") String statusType, @Bind("startTimestamp") Long startTimestamp, @Bind("endTimestamp") Long endTimestamp);

  @SqlUpdate("DELETE FROM STATUS_UPDATE")
  void truncateTable();

}

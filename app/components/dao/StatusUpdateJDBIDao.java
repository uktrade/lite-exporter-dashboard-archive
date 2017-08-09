package components.dao;

import models.StatusUpdate;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public interface StatusUpdateJDBIDao {

  @Mapper(StatusUpdateRSMapper.class)
  @SqlQuery("SELECT * FROM STATUS_UPDATE")
  List<StatusUpdate> getStatusUpdates();

  @Mapper(StatusUpdateRSMapper.class)
  @SqlQuery("SELECT * FROM STATUS_UPDATE WHERE APP_ID = :appId")
  List<StatusUpdate> getStatusUpdates(@Bind("appId") String appId);

  @SqlUpdate("INSERT INTO STATUS_UPDATE (APP_ID, STATUS_TYPE, START_TIMESTAMP, END_TIMESTAMP) VALUES (:appId, :statusType, :startTimestamp, :endTimestamp)")
  void insert(@Bind("appId") String appId, @Bind("statusType") String statusType, @Bind("startTimestamp") Long startTimestamp, @Bind("endTimestamp") Long endTimestamp);

  @SqlUpdate("DELETE FROM STATUS_UPDATE")
  void truncateTable();

}

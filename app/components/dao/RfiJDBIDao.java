package components.dao;

import java.util.List;
import models.Rfi;
import models.enums.RfiStatus;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

@UseStringTemplate3StatementLocator
public interface RfiJDBIDao {

  @Mapper(RfiRSMapper.class)
  @SqlQuery("SELECT * FROM RFI WHERE APP_ID in (<appIds>)")
  List<Rfi> getRfiList(@BindIn("appIds") List<String> appIds);

  @Mapper(RfiRSMapper.class)
  @SqlQuery("SELECT * FROM RFI WHERE APP_ID = :appId")
  List<Rfi> getRfiList(@Bind("appId") String appId);

  @SqlQuery("SELECT COUNT(*) FROM RFI WHERE APP_ID = :appId")
  int getRfiCount(@Bind("appId") String appId);

  @SqlUpdate("INSERT INTO RFI ( ID,  APP_ID, STATUS,  CREATED_TIMESTAMP, DUE_TIMESTAMP, SENT_BY, RECIPIENT_USER_IDS, MESSAGE) VALUES " +
      "                       (:id, :appId, :status, :createdTimestamp, :dueTimestamp, :sentBy, :recipientUserIds,  :message)")
  void insert(@Bind("id") String id,
              @Bind("appId") String appId,
              @Bind("status") RfiStatus rfiStatus,
              @Bind("createdTimestamp") Long createdTimestamp,
              @Bind("dueTimestamp") Long dueTimestamp,
              @Bind("sentBy") String sentBy,
              @Bind("recipientUserIds") String recipientUserIds,
              @Bind("message") String message);

  @SqlUpdate("DELETE FROM RFI")
  void truncateTable();

  @SqlUpdate("DELETE FROM RFI WHERE APP_ID = :appId")
  void deleteRfiListByAppId(@Bind("appId") String appId);

}

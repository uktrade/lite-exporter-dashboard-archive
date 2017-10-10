package components.dao;

import java.util.List;
import models.Outcome;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

@UseStringTemplate3StatementLocator
public interface OutcomeJDBIDao {

  @Mapper(OutcomeRSMapper.class)
  @SqlQuery("SELECT * FROM OUTCOME WHERE APP_ID in (<appIds>)")
  List<Outcome> getOutcomes(@BindIn("appIds") List<String> appIds);

  @Mapper(OutcomeRSMapper.class)
  @SqlQuery("SELECT * FROM OUTCOME WHERE APP_ID = :appId")
  List<Outcome> getOutcomes(@Bind("appId") String appId);

  @SqlUpdate("INSERT INTO OUTCOME (ID,  APP_ID, CREATED_BY_USER_ID, RECIPIENT_USER_IDS, CREATED_TIMESTAMP,  DOCUMENTS) VALUES " +
      "                          (:id, :appId, :createdByUserId,   :recipientUserIds,  :createdTimestamp,  :documents)")
  void insertOutcome(@Bind("id") String id,
                     @Bind("appId") String appId,
                     @Bind("createdByUserId") String createdByUserId,
                     @Bind("recipientUserIds") String recipientUserIds,
                     @Bind("createdTimestamp") Long createdTimestamp,
                     @Bind("documents") String documents);

  @SqlUpdate("DELETE FROM OUTCOME")
  void truncateTable();

  @SqlUpdate("DELETE FROM OUTCOME WHERE APP_ID = :appId")
  void deleteOutcomesByAppId(@Bind("appId") String appId);

}

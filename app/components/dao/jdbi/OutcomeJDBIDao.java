package components.dao.jdbi;

import components.dao.mapper.OutcomeRSMapper;
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
  @SqlQuery("SELECT * FROM OUTCOME WHERE CASE_REFERENCE in (<caseReferences>)")
  List<Outcome> getOutcomes(@BindIn("caseReferences") List<String> caseReferences);

  @SqlUpdate("INSERT INTO OUTCOME (ID,  CASE_REFERENCE, CREATED_BY_USER_ID, RECIPIENT_USER_IDS, CREATED_TIMESTAMP,  DOCUMENTS) VALUES " +
      "                          (:id, :caseReference, :createdByUserId,   :recipientUserIds,  :createdTimestamp,  :documents)")
  void insertOutcome(@Bind("id") String id,
                     @Bind("caseReference") String caseReference,
                     @Bind("createdByUserId") String createdByUserId,
                     @Bind("recipientUserIds") String recipientUserIds,
                     @Bind("createdTimestamp") Long createdTimestamp,
                     @Bind("documents") String documents);

  @SqlUpdate("DELETE FROM OUTCOME")
  void truncateTable();

  @SqlUpdate("DELETE FROM OUTCOME WHERE CASE_REFERENCE = :caseReference")
  void deleteOutcome(@Bind("caseReference") String caseReference);

}

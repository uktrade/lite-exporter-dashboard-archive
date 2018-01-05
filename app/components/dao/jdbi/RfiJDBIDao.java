package components.dao.jdbi;

import components.dao.mapper.RfiRSMapper;
import models.Rfi;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

@UseStringTemplate3StatementLocator
public interface RfiJDBIDao {

  @Mapper(RfiRSMapper.class)
  @SqlQuery("SELECT * FROM RFI WHERE CASE_REFERENCE in (<caseReferences>)")
  List<Rfi> getRfiList(@BindIn("caseReferences") List<String> caseReferences);

  @SqlUpdate("INSERT INTO RFI ( ID,  CASE_REFERENCE, CREATED_TIMESTAMP, DUE_TIMESTAMP, CREATED_BY_USER_ID, RECIPIENT_USER_IDS, MESSAGE) VALUES " +
      "                       (:id, :caseReference, :createdTimestamp, :dueTimestamp, :createdByUserId,   :recipientUserIds,  :message)")
  void insert(@Bind("id") String id,
              @Bind("caseReference") String caseReference,
              @Bind("createdTimestamp") Long createdTimestamp,
              @Bind("dueTimestamp") Long dueTimestamp,
              @Bind("createdByUserId") String createdByUserId,
              @Bind("recipientUserIds") String recipientUserIds,
              @Bind("message") String message);

  @SqlUpdate("UPDATE RFI SET DUE_TIMESTAMP = :dueTimestamp WHERE ID = :id")
  void updateDeadline(@Bind("id") String id,
                      @Bind("dueTimestamp") Long dueTimestamp);

  @SqlUpdate("DELETE FROM RFI")
  void truncateTable();

  @SqlUpdate("DELETE FROM RFI WHERE CASE_REFERENCE = :caseReference")
  void deleteRfiByCaseReference(@Bind("caseReference") String caseReference);

}

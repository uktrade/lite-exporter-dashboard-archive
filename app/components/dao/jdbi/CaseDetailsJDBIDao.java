package components.dao.jdbi;

import components.dao.mapper.CaseDetailsRSMapper;
import java.util.List;
import models.CaseDetails;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

@UseStringTemplate3StatementLocator
public interface CaseDetailsJDBIDao {

  @Mapper(CaseDetailsRSMapper.class)
  @SqlQuery("SELECT * FROM CASE_DETAILS WHERE APP_ID in (<appIds>)")
  List<CaseDetails> getCaseDetailsListByAppIds(@BindIn("appIds") List<String> appIds);

  @SqlUpdate("INSERT INTO CASE_DETAILS ( APP_ID, CASE_REFERENCE, CREATED_BY_USER_ID, CREATED_TIMESTAMP) "
      + "                       VALUES (:appId, :caseReference, :createdByUserId,   :createdTimestamp)")
  void insert(@Bind("appId") String appId,
              @Bind("caseReference") String caseReference,
              @Bind("createdByUserId") String createdByUserId,
              @Bind("createdTimestamp") Long createdTimestamp);

  @SqlUpdate("DELETE FROM CASE_DETAILS WHERE CASE_REFERENCE = :caseReference")
  void deleteCaseDetails(@Bind("caseReference") String caseReference);

  @SqlUpdate("DELETE FROM CASE_DETAILS")
  void truncateTable();

}

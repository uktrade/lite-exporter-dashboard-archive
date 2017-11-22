package components.dao.jdbi;

import components.dao.mapper.SielRSMapper;
import java.util.List;
import models.Siel;
import models.enums.SielStatus;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

@UseStringTemplate3StatementLocator
public interface SielJDBIDao {

  @Mapper(SielRSMapper.class)
  @SqlQuery("SELECT * FROM SIEL WHERE CUSTOMER_ID in (<customerIds>)")
  List<Siel> getSiels(@BindIn("customerIds") List<String> customerIds);

  @Mapper(SielRSMapper.class)
  @SqlQuery("SELECT * FROM SIEL WHERE CASE_REFERENCE = :caseReference")
  Siel getSiel(@Bind("caseReference") String caseReference);

  @SqlUpdate("INSERT INTO SIEL (ID,  CUSTOMER_ID, APPLICANT_REFERENCE, CASE_REFERENCE, ISSUE_TIMESTAMP, EXPIRY_TIMESTAMP, STATUS,  SITE_ID, DESTINATION_LIST) VALUES" +
      "                       (:id, :customerId, :applicantReference, :caseReference, :issueTimestamp, :expiryTimestamp, :status, :siteId, :destinationList)")
  void insertSiel(@Bind("id") String id,
                  @Bind("customerId") String customerId,
                  @Bind("applicantReference") String applicantReference,
                  @Bind("caseReference") String caseReference,
                  @Bind("issueTimestamp") Long issueTimestamp,
                  @Bind("expiryTimestamp") Long expiryTimestamp,
                  @Bind("status") SielStatus sielStatus,
                  @Bind("siteId") String siteId,
                  @Bind("destinationList") String destinationList);

  @SqlUpdate("DELETE FROM SIEL")
  void truncateTable();

  @SqlUpdate("DELETE FROM SIEL WHERE CUSTOMER_ID = :customerId")
  void deleteSielsByCustomerId(@Bind("customerId") String customerId);

}

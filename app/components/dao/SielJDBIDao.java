package components.dao;

import models.Siel;
import models.enums.SielStatus;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

@UseStringTemplate3StatementLocator
public interface SielJDBIDao {

  @Mapper(SielRSMapper.class)
  @SqlQuery("SELECT * FROM SIEL WHERE COMPANY_ID in (<customerIds>)")
  List<Siel> getSiels(@BindIn("customerIds") List<String> customerIds);

  @Mapper(SielRSMapper.class)
  @SqlQuery("SELECT * FROM SIEL WHERE CASE_REFERENCE = :caseReference")
  Siel getSiel(@Bind("caseReference") String caseReference);

  @SqlUpdate("INSERT INTO SIEL ( SIEL_ID, COMPANY_ID, APPLICANT_REFERENCE, CASE_REFERENCE, ISSUE_TIMESTAMP, EXPIRY_TIMESTAMP, STATUS,  SITE_ID, DESTINATION_LIST) VALUES" +
      "                        (:sielId, :companyId, :applicantReference, :caseReference, :issueTimestamp, :expiryTimestamp, :status, :siteId, :destinationList)")
  void insertSiel(@Bind("sielId") String sielId,
                  @Bind("companyId") String companyId,
                  @Bind("applicantReference") String applicantReference,
                  @Bind("caseReference") String caseReference,
                  @Bind("issueTimestamp") Long issueTimestamp,
                  @Bind("expiryTimestamp") Long expiryTimestamp,
                  @Bind("status") SielStatus sielStatus,
                  @Bind("siteId") String siteId,
                  @Bind("destinationList") String destinationList);

  @SqlUpdate("DELETE FROM SIEL")
  void truncateTable();

  @SqlUpdate("DELETE FROM SIEL WHERE COMPANY_ID IN (<companyIds>)")
  void deleteSielsByCompanyIds(List<String> companyIds);

}
